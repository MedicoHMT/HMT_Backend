package com.example.hmt.core.config;

import com.example.hmt.core.auth.model.Role;
import com.example.hmt.core.auth.model.UserPermission;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RolePermissionConfig {

    private static final Map<Role, Set<UserPermission>> ROLE_DEFAULTS = new HashMap<>();

    static {
        // --- DEFINE DOCTOR PERMISSIONS ---
        ROLE_DEFAULTS.put(Role.DOCTOR, Set.of(
                UserPermission.PATIENT_READ,
                UserPermission.PATIENT_WRITE,
                UserPermission.APPOINTMENT_READ,
                UserPermission.LAB_RESULT_VIEW
        ));

        // --- DEFINE STAFF PERMISSIONS ---
        ROLE_DEFAULTS.put(Role.STAFF, Set.of(
                UserPermission.PATIENT_READ, // Staff might only read, not write
                UserPermission.APPOINTMENT_READ,
                UserPermission.APPOINTMENT_WRITE
        ));
    }

    // 2. Helper method to get permissions safely
    public static Set<UserPermission> getDefaultsFor(Role role) {
        return ROLE_DEFAULTS.getOrDefault(role, Set.of());
    }
}
