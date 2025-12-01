package com.example.hmt.core.auth.model;

public enum UserPermission {
    // Admin features
    ADMIN_DASHBOARD,
    MANAGE_USERS,

    // Doctor/Staff features
    PATIENT_READ,
    PATIENT_WRITE,
    APPOINTMENT_MANAGE,
    APPOINTMENT_READ,
    APPOINTMENT_WRITE,
    LAB_RESULT_VIEW
}
