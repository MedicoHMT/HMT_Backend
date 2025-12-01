package com.example.hmt.core.otp;


public interface OtpStore {
    boolean isLocked(String key);
    long incrRequestCount(String key, int ttlSeconds);
    long incrIpCount(String key, int ttlSeconds);
    void storeOtpHash(String key, String hash, int ttlSeconds);
    String getOtpHash(String key);
    void deleteOtp(String key);
    long incrAttempts(String key, int ttlSeconds);
    void setLock(String key, int ttlSeconds);
}