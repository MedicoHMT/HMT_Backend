package com.example.hmt.core.auth.controller;

import com.example.hmt.core.auth.service.OtpService;
import com.example.hmt.core.auth.service.RefreshTokenService;
import com.example.hmt.core.config.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth/superadmin")
public class SuperAdminAuthController {

    private final OtpService otpService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final String cookieName;
    private final String cookiePath;
    private final String sameSite;
    private final boolean secureFlag = true; // set true for prod (HTTPS)

    public SuperAdminAuthController(OtpService otpService, JwtService jwtService, RefreshTokenService refreshTokenService, org.springframework.core.env.Environment env) {
        this.otpService = otpService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.cookieName = env.getProperty("jwt.refresh.cookie-name", "refresh");
        this.cookiePath = env.getProperty("jwt.refresh.cookie-path", "/");
        this.sameSite = env.getProperty("jwt.refresh.cookie-samesite", "Strict");
    }

    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(@RequestBody Req req, HttpServletRequest httpReq) {
        // Always respond 200 to avoid enumeration
        try {
            otpService.requestOtp(req.email.trim().toLowerCase(), httpReq.getRemoteAddr());
        } catch (Exception e) {
            // log but do not leak to client
            System.out.println("OTP request issue: " + e.getMessage());
        }
        return ResponseEntity.ok("If that account exists, an OTP has been sent.");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyReq req, HttpServletResponse httpRes) {
        try {
            // otpService now returns user's email on success and JwtService used here to generate access token
            String email = otpService.verifyOtpAndReturnEmail(req.email.trim().toLowerCase(), req.otp);
            // generate access token
            String access = jwtService.generateTokenSuperAdmin(email, "SUPER_ADMIN");
            // create refresh jti and store in redis
            String jti = refreshTokenService.createRefreshForUser(email);

            // set HttpOnly cookie
            ResponseCookie cookie = ResponseCookie.from(cookieName, jti).httpOnly(true).secure(secureFlag).path(cookiePath).sameSite(sameSite).maxAge(Long.parseLong(System.getProperty("jwt.refresh.expiration.seconds", "604800"))) // fallback
                    .build();
            httpRes.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            return ResponseEntity.ok(Map.of("accessToken", access));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body("Invalid OTP");
        } catch (IllegalStateException e) {
            if ("account_locked".equals(e.getMessage())) return ResponseEntity.status(429).body("Account locked");
            return ResponseEntity.status(400).body("OTP expired or invalid");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        jakarta.servlet.http.Cookie cookie = org.springframework.web.util.WebUtils.getCookie(request, cookieName);
        if (cookie == null) return ResponseEntity.status(401).body("no_refresh_cookie");

        String oldJti = cookie.getValue();
        var optUser = refreshTokenService.validateRefresh(oldJti);
        if (optUser.isEmpty()) {
            // possible theft or expired
            return ResponseEntity.status(401).body("invalid_refresh");
        }

        // rotate
        var newJtiOpt = refreshTokenService.rotate(oldJti);
        if (newJtiOpt.isEmpty()) return ResponseEntity.status(401).body("invalid_refresh");

        String userEmail = optUser.get();
        String newJti = newJtiOpt.get();
        // issue new access token
        String newAccess = jwtService.generateTokenSuperAdmin(userEmail, "SUPER_ADMIN");

        // set new cookie
        ResponseCookie cookieNew = ResponseCookie.from(cookieName, newJti).httpOnly(true).secure(secureFlag).path(cookiePath).sameSite(sameSite).maxAge(Long.parseLong(System.getProperty("jwt.refresh.expiration.seconds", "604800"))) // fallback
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookieNew.toString());

        return ResponseEntity.ok(Map.of("accessToken", newAccess));
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        jakarta.servlet.http.Cookie cookie = org.springframework.web.util.WebUtils.getCookie(request, cookieName);
        if (cookie != null) {
            refreshTokenService.revoke(cookie.getValue());
        }
        // clear cookie
        ResponseCookie expired = ResponseCookie.from(cookieName, "").httpOnly(true).secure(secureFlag).path(cookiePath).maxAge(0).sameSite(sameSite).build();
        response.addHeader(HttpHeaders.SET_COOKIE, expired.toString());
        return ResponseEntity.ok().build();
    }


    static class Req {
        public String email;
    }

    static class VerifyReq {
        public String email;
        public String otp;
    }

}
