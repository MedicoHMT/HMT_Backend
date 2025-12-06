package com.example.hmt.core.auth;

import com.example.hmt.core.auth.dto.RegisterDoctorPerHospitalDTO;
import com.example.hmt.core.auth.dto.RegisterUserDTO;
import com.example.hmt.core.auth.dto.RegisterUserPerHospitalDTO;
import com.example.hmt.core.auth.model.User;
import com.example.hmt.core.auth.repository.UserRepository;
import com.example.hmt.core.auth.service.OtpService;
import com.example.hmt.core.auth.service.RefreshTokenService;
import com.example.hmt.core.config.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {


    private final AuthService authService;
    private final OtpService otpService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;


    @Value("${jwt.refresh.cookie-name:refresh}")
    private String cookieName;

    @Value("${jwt.refresh.cookie-path:/}")
    private String cookiePath;

    @Value("${jwt.refresh.cookie-samesite:Strict}")
    private String sameSite;

    @Value("${jwt.refresh.cookie-secure:true}")
    private boolean secureFlag;

    @Value("${jwt.refresh.expiration.seconds:604800}")
    private long refreshDurationSeconds;


    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/api/auth/registerAdminHospital")
    public ResponseEntity<String> register(@RequestBody RegisterUserDTO dto) {
        authService.registerUserAdminAndHospital(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User Registered Successfully!");
    }


    @PostMapping("/api/auth/loginUser")
    public ResponseEntity<Map<String, String>> loginUser(
            @RequestParam String value,
            @RequestParam String type,
            HttpServletRequest httpReq) {

        //  Basic Validation
        if (value == null || value.trim().isEmpty() || type == null) {
            throw new IllegalArgumentException("Value and Type are required");
        }

        //  Call Service
        if (type.equalsIgnoreCase("username")) {
            otpService.requestOtp(null, httpReq.getRemoteAddr(), null, value);
        } else if (type.equalsIgnoreCase("email")) {
            otpService.requestOtp(value, httpReq.getRemoteAddr(), null, null);
        } else {
            throw new IllegalArgumentException("Invalid login type. Use 'email' or 'username'");
        }

        return ResponseEntity.ok(Map.of("message", "If that account exists, an OTP has been sent."));
    }

    @PostMapping("/api/auth/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String value,
                                       @RequestParam String type,
                                       @RequestParam String otp,
                                       HttpServletResponse httpRes) {

        try {
            User user;

            // Validate Input
            if (value == null || type == null || otp == null) {
                return ResponseEntity.badRequest().body("Missing required parameters");
            }

            if (type.equalsIgnoreCase("username")) {
                user = (User) otpService.verifyOtpAndReturnEmail(null, otp, null, value.trim());
            } else if (type.equalsIgnoreCase("email")) {
                user = (User) otpService.verifyOtpAndReturnEmail(value.trim(), otp, null, null);
            } else {
                return ResponseEntity.badRequest().body("Invalid login type");
            }

            // Generate Access Token
            String access = jwtService.generateToken(user);

            // Create Refresh Token
            String jti = refreshTokenService.createRefreshForUser(user.getUsername());

            // Set Cookie
            ResponseCookie cookie = ResponseCookie.from(cookieName, jti)
                    .httpOnly(true)
                    .secure(secureFlag)
                    .path(cookiePath)
                    .sameSite(sameSite)
                    .maxAge(Long.parseLong(System.getProperty("jwt.refresh.expiration.seconds", "604800")))
                    .build();

            httpRes.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok(Map.of("accessToken", access));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body("Invalid OTP or Credentials");
        } catch (IllegalStateException e) {
            if ("account_locked".equals(e.getMessage())) {
                return ResponseEntity.status(429).body("Account locked due to too many failed attempts");
            }
            return ResponseEntity.status(400).body("OTP expired");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error");
        }
    }


    @PostMapping("/api/auth/refresh")
    @Transactional()
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {

        // Extract Cookie
        jakarta.servlet.http.Cookie cookie = org.springframework.web.util.WebUtils.getCookie(request, cookieName);
        if (cookie == null) return ResponseEntity.status(401).body("no_refresh_cookie");

        String oldJti = cookie.getValue();

        // Validate Refresh Token
        var optSubject = refreshTokenService.validateRefresh(oldJti);
        if (optSubject.isEmpty()) {
            return ResponseEntity.status(401).body("invalid_refresh");
        }

        // Rotate Token
        var newJtiOpt = refreshTokenService.rotate(oldJti);
        if (newJtiOpt.isEmpty()) return ResponseEntity.status(401).body("invalid_refresh");

        String usernameOrEmail = optSubject.get();
        String newJti = newJtiOpt.get();

        // FETCH USER ENTITY
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new BadCredentialsException("User not found during refresh"));

        // Generate New Access Token
        String newAccessToken = jwtService.generateToken(user);

        // Set New Cookie
        ResponseCookie cookieNew = ResponseCookie.from(cookieName, newJti)
                .httpOnly(true)
                .secure(secureFlag)
                .path(cookiePath)
                .sameSite(sameSite)
                .maxAge(refreshDurationSeconds)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookieNew.toString());

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/api/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        jakarta.servlet.http.Cookie cookie = org.springframework.web.util.WebUtils.getCookie(request, cookieName);
        if (cookie != null) {
            refreshTokenService.revoke(cookie.getValue());
        }
        // clear cookie
        ResponseCookie expired = ResponseCookie
                .from(cookieName, "")
                .httpOnly(true).secure(secureFlag)
                .path(cookiePath)
                .maxAge(0)
                .sameSite(sameSite)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, expired.toString());
        return ResponseEntity.ok().build();
    }


    // ADMINs can register users for their own hospital
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/v1/admin/register")
    public ResponseEntity<String> registerForAdminHospital(@RequestBody RegisterUserPerHospitalDTO dto) {
        String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        String message = authService.registerForUserInHospital(adminUsername, dto);
        return ResponseEntity.ok(message);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/v1/admin/registerDoctor")
    public ResponseEntity<String> registerForDoctorHospital(@RequestBody RegisterDoctorPerHospitalDTO dto) {
        String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        String message = authService.registerForDoctorHospital(adminUsername, dto);
        return ResponseEntity.ok(message);
    }
}
