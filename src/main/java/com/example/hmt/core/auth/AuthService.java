package com.example.hmt.core.auth;

import com.example.hmt.core.auth.dto.RegisterUserDTO;
import com.example.hmt.core.auth.dto.RegisterUserPerHospitalDTO;
import com.example.hmt.core.auth.model.Role;
import com.example.hmt.core.auth.model.User;
import com.example.hmt.core.auth.model.UserPermission;
import com.example.hmt.core.auth.repository.UserRepository;
import com.example.hmt.core.config.RolePermissionConfig;
import com.example.hmt.core.tenant.Hospital;
import com.example.hmt.core.tenant.HospitalRepository;
import com.example.hmt.core.tenant.TenantContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;


    @Transactional
    public void registerUserAdminAndHospital(RegisterUserDTO dto) {

        // Security Check
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new BadCredentialsException("User is not authenticated");
        }

        // Validation (Fail Fast)
        // Validate email BEFORE saving the hospital.
        // If this fails, nothing is saved to the DB.
        if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required for registration.");
        }
        if (dto.getUsername() == null || dto.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username is required for registration.");
        }

        // Check if hospital code already exist
        if (hospitalRepository.existsByHospitalCode(dto.getHospitalCode())) {
            throw new IllegalArgumentException("Hospital Code already exists.");
        }


        Hospital hospital = Hospital.builder()
                .hospitalCode(dto.getHospitalCode())
                .name(dto.getHospitalName())
                .address(dto.getHospitalAddress())
                .build();

        hospitalRepository.save(hospital);


        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phoneNumber(dto.getPhoneNumber())
                .role(Role.ADMIN)
                .hospital(hospital)
                // grant all permissions to ADMIN
                .permissions(EnumSet.allOf(UserPermission.class))
                .build();

        userRepository.save(user);
    }


    // Register a user into the authenticated ADMIN's hospital
    @Transactional
    public String registerForUserInHospital(String adminUsername, RegisterUserPerHospitalDTO dto) {

        // Validate Context
        Long currentHospitalId = TenantContext.getHospitalId();
        if (currentHospitalId == null) {
            throw new IllegalStateException("System Error: No hospital context found.");
        }

        // Fetch Admin
        User admin = userRepository.findByUsernameAndHospitalId(adminUsername, currentHospitalId)
                .orElseThrow(() -> new IllegalArgumentException("Admin permission denied for this hospital"));


        // Validate Duplicate User
        if (userRepository.existsByUsernameAndHospitalId(dto.getUsername(), currentHospitalId)) {
            throw new IllegalArgumentException("Username '" + dto.getUsername() + "' already exists in this hospital.");
        }


        Set<UserPermission> initialPermissions = RolePermissionConfig.getDefaultsFor(dto.getRole());

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phoneNumber(dto.getPhoneNumber())
                .hospital(admin.getHospital())
                .role(dto.getRole())
                .permissions(new HashSet<>(initialPermissions))
                .build();

        userRepository.save(user);

        return "User Registered Successfully!";
    }


}
