package com.example.hmt.core.auth.service;

import com.example.hmt.core.auth.model.SuperAdmin;
import com.example.hmt.core.auth.repository.SuperAdminRepository;
import com.example.hmt.core.otp.OtpStore;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class OtpService {
    private final SuperAdminRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final java.security.SecureRandom rnd = new java.security.SecureRandom();

    private final Duration OTP_TTL;
    private final int OTP_DIGITS;
    private final int MAX_ATTEMPTS;
    private final Duration LOCK_DURATION;
    private final int REQUEST_LIMIT_PER_HOUR;


    private final OtpStore otpStore;

    public OtpService(SuperAdminRepository repo, org.springframework.core.env.Environment env, OtpStore otpStoreOpt) {
        this.repo = repo;
        this.otpStore = otpStoreOpt;

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

    public void requestOtp(String email, String ip) {
        email = email.trim().toLowerCase();
        Optional<SuperAdmin> opt = repo.findByEmail(email);
        if (opt.isEmpty()) {
            return;
        }
        String key = makeKeyForEmail(email);

        if (otpStore.isLocked(key)) return;

        // increment per-email requests (ttl 1 hour)
        long reqs = otpStore.incrRequestCount(key, 3600);
        if (reqs > REQUEST_LIMIT_PER_HOUR) {
            otpStore.setLock(key, (int) LOCK_DURATION.getSeconds());
            return;
        }

        // per-IP limit (optional)
        String ipKey = makeKeyForEmail(email) + ":IP:" + ip;
        long ipReqs = otpStore.incrIpCount(ipKey, 3600);
        if (ipReqs > 100) { // very permissive; tune as needed
            return;
        }

        // generate OTP and store hashed OTP in redis
        String otp = generateNumericOtp(OTP_DIGITS);
        String hash = encoder.encode(otp);
        otpStore.storeOtpHash(key, hash, (int) OTP_TTL.getSeconds());

        // DEV: print OTP. Replace with SES/Twilio sending.
        System.out.println("DEV OTP red for " + email + ": " + otp);
    }

    public String verifyOtpAndReturnEmail(String email, String otp) {
        email = email.trim().toLowerCase();
        Optional<SuperAdmin> opt = repo.findByEmail(email);
        if (opt.isEmpty()) throw new IllegalArgumentException("invalid");
        SuperAdmin admin = opt.get();

        String key = makeKeyForEmail(email);
        if (otpStore.isLocked(key)) throw new IllegalStateException("account_locked");

        String hash = otpStore.getOtpHash(key);
        if (hash == null) throw new IllegalStateException("otp_expired");

        boolean ok = encoder.matches(otp, hash);
        if (!ok) {
            long attempts = otpStore.incrAttempts("attempts:" + key, (int) OTP_TTL.getSeconds());
            if (attempts >= MAX_ATTEMPTS) {
                otpStore.setLock(key, (int) LOCK_DURATION.getSeconds());
                otpStore.deleteOtp(key);
                throw new IllegalStateException("account_locked");
            }
            throw new IllegalArgumentException("invalid");
        }

        // success
        otpStore.deleteOtp(key);
        // update last_login in DB
        admin.setLastLogin(Instant.now());
        repo.save(admin);
        return email;

    }

    private String generateNumericOtp(int digits) {
        int bound = (int) Math.pow(10, digits);
        int min = bound / 10;
        int value = rnd.nextInt(bound - min) + min;
        return String.valueOf(value);
    }
}