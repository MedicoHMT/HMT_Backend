package com.example.hmt.core.auth.controller;

import com.example.hmt.core.auth.service.OtpService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/superadmin")
public class SuperAdminAuthController {

    private final OtpService otpService;

    public SuperAdminAuthController(OtpService otpService) {
        this.otpService = otpService;
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
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyReq req) {
        try {
            String token = otpService.verifyOtp(req.email.trim().toLowerCase(), req.otp);
            return ResponseEntity.ok(new TokenResp(token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body("Invalid OTP");
        } catch (IllegalStateException e) {
            if ("account_locked".equals(e.getMessage())) return ResponseEntity.status(429).body("Account locked");
            return ResponseEntity.status(400).body("OTP expired or invalid");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error");
        }
    }

    static class Req {
        public String email;
    }

    static class VerifyReq {
        public String email;
        public String otp;
    }

    static class TokenResp {
        public String token;

        public TokenResp(String t) {
            token = t;
        }
    }

}
