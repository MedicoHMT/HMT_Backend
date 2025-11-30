package com.example.hmt.core.auth.service;

import com.example.hmt.core.auth.model.Role;
import com.example.hmt.core.auth.model.SuperAdmin;
import com.example.hmt.core.auth.model.User;
import com.example.hmt.core.auth.repository.SuperAdminRepository;
import com.example.hmt.core.auth.repository.UserRepository;
import com.example.hmt.core.otp.OtpStore;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class OtpService {
    private final SuperAdminRepository superAdminRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final java.security.SecureRandom rnd = new java.security.SecureRandom();

    private final Duration OTP_TTL;
    private final int OTP_DIGITS;
    private final int MAX_ATTEMPTS;
    private final Duration LOCK_DURATION;
    private final int REQUEST_LIMIT_PER_HOUR;


    private final OtpStore otpStore;

    public OtpService(SuperAdminRepository superAdminRepository, UserRepository userRepository, org.springframework.core.env.Environment env, OtpStore otpStoreOpt) {
        this.superAdminRepository = superAdminRepository;
        this.otpStore = otpStoreOpt;
        this.userRepository = userRepository;

        this.OTP_TTL = Duration.ofSeconds(Integer.parseInt(env.getProperty("otp.ttl.seconds", "600")));
        this.OTP_DIGITS = Integer.parseInt(env.getProperty("otp.digits", "6"));
        this.MAX_ATTEMPTS = Integer.parseInt(env.getProperty("otp.max-attempts", "5"));
        this.LOCK_DURATION = Duration.ofSeconds(Integer.parseInt(env.getProperty("otp.lock-duration.seconds", "900")));
        this.REQUEST_LIMIT_PER_HOUR = Integer.parseInt(env.getProperty("otp.request-limit-per-hour", "5"));
    }

    // helper to build consistent key for superAdmin
    private String makeKeyForEmail(String email) {
        return "GLOBAL:" + email;
    }

    public void requestOtp(String email, String ip, Role role, String username) {

        String targetEmail = null;

        // --- RESOLVE THE REAL EMAIL ---
        if (role == Role.SUPER_ADMIN) {
            if (email == null) return;
            // Check SuperAdmin
            Optional<SuperAdmin> opt = superAdminRepository.findByEmail(email.trim().toLowerCase());
            if (opt.isPresent()) {
                targetEmail = opt.get().getEmail();
            }
        } else {
            // Check User
            Optional<User> userOpt = Optional.empty();

            if (username != null && !username.trim().isEmpty()) {
                userOpt = userRepository.findByUsername(username.trim().toLowerCase());
            } else if (email != null && !email.trim().isEmpty()) {
                userOpt = userRepository.findByEmail(email.trim().toLowerCase());
            }

            if (userOpt.isPresent()) {
                targetEmail = userOpt.get().getEmail();
            }
        }

        // If user not found, we silently return
        if (targetEmail == null) {
            return;
        }

        // --- RATE LIMITING & OTP GENERATION ---
        String key = makeKeyForEmail(targetEmail);

        // Check if account is locked
        if (otpStore.isLocked(key)) {
            throw new IllegalStateException("account_locked");
        }

        // Rate Limit: Per User (Email)
        long reqs = otpStore.incrRequestCount(key, 3600);
        if (reqs > REQUEST_LIMIT_PER_HOUR) {
            otpStore.setLock(key, (int) LOCK_DURATION.getSeconds());
            throw new IllegalStateException("Too many requests. Account locked for 15 minutes.");
        }

        // Rate Limit: Per IP Address
        String ipKey = "IP:" + ip;
        long ipReqs = otpStore.incrIpCount(ipKey, 3600);
        if (ipReqs > 100) {
            return; // Silently ignore spam from this IP
        }

        // Generate & Store
        String otp = generateNumericOtp(OTP_DIGITS);
        String hash = encoder.encode(otp);

        otpStore.storeOtpHash(key, hash, (int) OTP_TTL.getSeconds());

        // Send Email
        System.out.println("DEV: OTP for " + targetEmail + " is: " + otp);
    }


    @Transactional
    public Object verifyOtpAndReturnEmail(String email, String otp, Role role, String username) {

        String targetEmail = null;
        User userEntity = null;
        SuperAdmin adminEntity = null;

        // ---  RESOLVE USER ---
        if (role == Role.SUPER_ADMIN) {
            if (email == null) throw new IllegalArgumentException("invalid");

            // Find SuperAdmin
            adminEntity = superAdminRepository.findByEmail(email.trim().toLowerCase())
                    .orElseThrow(() -> new IllegalArgumentException("invalid"));

            targetEmail = adminEntity.getEmail();
        } else {
            // Find  User
            Optional<User> opt = Optional.empty();

            if (email != null && !email.trim().isEmpty()) {
                opt = userRepository.findByEmail(email.trim().toLowerCase());
            } else if (username != null && !username.trim().isEmpty()) {
                opt = userRepository.findByUsername(username.trim().toLowerCase());
            }

            userEntity = opt.orElseThrow(() -> new IllegalArgumentException("invalid"));
            targetEmail = userEntity.getEmail();
        }

        // --- VERIFY OTP ---
        String key = makeKeyForEmail(targetEmail);

        if (otpStore.isLocked(key)) throw new IllegalStateException("account_locked");

        String hash = otpStore.getOtpHash(key);
        if (hash == null) throw new IllegalStateException("otp_expired");

        if (!encoder.matches(otp, hash)) {
            // Handle Failure
            long attempts = otpStore.incrAttempts("attempts:" + key, (int) OTP_TTL.getSeconds());
            if (attempts >= MAX_ATTEMPTS) {
                otpStore.setLock(key, (int) LOCK_DURATION.getSeconds());
                otpStore.deleteOtp(key);
                throw new IllegalStateException("account_locked");
            }
            throw new IllegalArgumentException("invalid");
        }

        // --- SUCCESS ---
        otpStore.deleteOtp(key);

        if (role == Role.SUPER_ADMIN) {
            adminEntity.setLastLogin(Instant.now());
            superAdminRepository.save(adminEntity);
            return targetEmail;
        } else {
            userEntity.setLastLogin(Instant.now());
            userRepository.save(userEntity);
            return userEntity;
        }
    }

    private String generateNumericOtp(int digits) {
        int bound = (int) Math.pow(10, digits);
        int min = bound / 10;
        int value = rnd.nextInt(bound - min) + min;
        return String.valueOf(value);
    }
}