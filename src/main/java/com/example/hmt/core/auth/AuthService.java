package com.example.hmt.core.auth;

import com.example.hmt.core.auth.dto.RegisterDoctorPerHospitalDTO;
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
import com.example.hmt.department.model.Department;
import com.example.hmt.department.repository.DepartmentRepository;
import com.example.hmt.doctor.Doctor;
import com.example.hmt.doctor.DoctorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;

    /**
     * Register hospital + admin (existing implementation, optimized).
     */
    @Transactional
    public void registerUserAdminAndHospital(RegisterUserDTO dto) {
        // Security check
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new BadCredentialsException("User is not authenticated");
        }

        // Validate essential dto fields early (fail-fast)
        requireNonEmpty(dto.getEmail(), "Email is required for registration.");
        requireNonEmpty(dto.getUsername(), "Username is required for registration.");
        if (hospitalRepository.existsByHospitalCode(dto.getHospitalCode())) {
            throw new IllegalArgumentException("Hospital Code already exists.");
        }

        // Create Hospital
        Hospital hospital = Hospital.builder()
                .hospitalCode(dto.getHospitalCode())
                .name(dto.getHospitalName())
                .address(dto.getHospitalAddress())
                .build();
        hospital = hospitalRepository.save(hospital);

        // Build and save user (admin) — assign all permissions
        User admin = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phoneNumber(dto.getPhoneNumber())
                .role(Role.ADMIN)
                .hospital(hospital)
                .permissions(EnumSet.allOf(UserPermission.class))
                .build();

        userRepository.save(admin);

    }

    /**
     * Register a non-doctor user inside the current tenant hospital (ADMIN caller).
     */
    @Transactional
    public String registerForUserInHospital(String adminUsername, RegisterUserPerHospitalDTO dto) {
        Long hospitalId = ensureHospitalContext();
        User admin = fetchAdminForHospital(adminUsername, hospitalId);

        ensureUsernameAvailable(dto.getUsername(), hospitalId);

        Set<UserPermission> initialPerms = RolePermissionConfig.getDefaultsFor(dto.getRole());

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phoneNumber(dto.getPhoneNumber())
                .hospital(admin.getHospital())
                .role(dto.getRole())
                .permissions(new HashSet<>(initialPerms))
                .build();

        userRepository.save(user);

        return "User Registered Successfully!";
    }

    /**
     * Register a doctor inside the current tenant hospital (ADMIN caller).
     * Creates both User and Doctor rows and ensures department/hospital consistency.
     */
    @Transactional
    public String registerForDoctorHospital(String adminUsername, RegisterDoctorPerHospitalDTO dto) {
        Long hospitalId = ensureHospitalContext();
        User admin = fetchAdminForHospital(adminUsername, hospitalId);

        ensureUsernameAvailable(dto.getUsername(), hospitalId);

        Department department = null;
        if (dto.getDepartment_id() != null) {
            department = departmentRepository.findById(dto.getDepartment_id())
                    .orElseThrow(() -> new IllegalArgumentException("Department not found: " + dto.getDepartment_id()));
            if (!Objects.equals(department.getHospitalId(), hospitalId)) {
                throw new IllegalArgumentException("Department does not belong to the current hospital.");
            }
        }

        Set<UserPermission> initialPerms = RolePermissionConfig.getDefaultsFor(Role.DOCTOR);

        // Create and persist user
        User user = createAndSaveUser(
                dto.getUsername(),
                dto.getEmail(),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getPhoneNumber(),
                Role.DOCTOR,
                admin.getHospital(),
                initialPerms
        );

        // Create and persist doctor
        Doctor doctor = Doctor.builder()
                .user(user)
                .department(department)
                .specialization(dto.getSpecialization())
                .consultation_fee(dto.getConsultation_fee())
                .build();


        doctorRepository.save(doctor);

        return "Doctor Registered Successfully!";
    }

    // -------------------------
    // Private helpers
    // -------------------------

    private Long ensureHospitalContext() {
        Long currentHospitalId = TenantContext.getHospitalId();
        if (currentHospitalId == null) {
            throw new IllegalStateException("System Error: No hospital context found.");
        }
        return currentHospitalId;
    }

    private User fetchAdminForHospital(String adminUsername, Long hospitalId) {
        return userRepository.findByUsernameAndHospitalId(adminUsername, hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("Admin permission denied for this hospital"));
    }

    private void ensureUsernameAvailable(String username, Long hospitalId) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (userRepository.existsByUsernameAndHospitalId(username, hospitalId)) {
            throw new IllegalArgumentException("Username '" + username + "' already exists in this hospital.");
        }
    }

    private User createAndSaveUser(String username,
                                   String email,
                                   String firstName,
                                   String lastName,
                                   Long phoneNumber,
                                   Role role,
                                   Hospital hospital,
                                   Set<UserPermission> permissions) {

        User user = User.builder()
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .role(role)
                .hospital(hospital)
                .permissions(new HashSet<>(permissions))
                .build();

        user = userRepository.save(user);

        return user;
    }


    private void requireNonEmpty(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
    }
}
