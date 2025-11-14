package com.example.hmt.core.tenant;

public class TenantContext {

    private static final ThreadLocal<Long> hospitalId = new ThreadLocal<>();

    public static void setHospitalId(Long id) {
        hospitalId.set(id);
    }

    public static Long getHospitalId() {
        return hospitalId.get();
    }

    public static void clear() {
        hospitalId.remove();
    }
}
