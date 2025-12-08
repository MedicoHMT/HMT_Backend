package com.example.hmt.core.tenant;

import java.util.UUID;

public class TenantContext {

    private static final ThreadLocal<Long> hospitalId = new ThreadLocal<>();
    private static final ThreadLocal<UUID> userId = new ThreadLocal<>();

    public static void setHospitalId(Long id) {
        hospitalId.set(id);
    }

    public static Long getHospitalId() {
        return hospitalId.get();
    }
    public static void setUserId(UUID id) {
        userId.set(id);
    }

    public static UUID getUserId() {
        return userId.get();
    }

    public static void clear() {
        hospitalId.remove();
        userId.remove();
    }
}
