package com.example.hmt.core.auth;

import com.example.hmt.core.auth.dto.AuthRequestDTO;
import com.example.hmt.core.auth.dto.AuthResponseDTO;
import com.example.hmt.core.auth.dto.RegisterUserDTO;
import com.example.hmt.core.auth.dto.RegisterUserPerHospitalDTO;
import com.example.hmt.core.auth.model.Role;
import com.example.hmt.core.auth.model.User;
import com.example.hmt.core.auth.repository.UserRepository;
import com.example.hmt.core.config.JwtService;
import com.example.hmt.core.tenant.Hospital;
import com.example.hmt.core.tenant.HospitalRepository;
import com.example.hmt.core.tenant.TenantContext;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final HospitalRepository hospitalRepository;

    // Read super-admin credentials from configuration (don't hard-code secrets)
    @Value("${superadmin.username}")
    private String configuredSuperAdminUsername;

    @Value("${superadmin.password}")
    private String configuredSuperAdminPlainPassword;

    // We'll store the hashed version of the configured password
    private String configuredSuperAdminPasswordHash;

    @PostConstruct
    private void init() {
        if (configuredSuperAdminPlainPassword != null) {
            configuredSuperAdminPasswordHash = passwordEncoder.encode(configuredSuperAdminPlainPassword);
        }
    }

    public String register(RegisterUserDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new BadCredentialsException("Not authenticated");
        }

        if (!configuredSuperAdminUsername.equals(auth.getName())) {
            throw new BadCredentialsException("Only Super Admin can register users");
        }

        Long hospitalId = dto.getHospitalId();
        String hospitalName = dto.getHospitalName();
        if (hospitalId == null && (hospitalName == null || hospitalName.isBlank())) {
            throw new BadCredentialsException("Hospital Required");
        }

        // If hospitalId not provided, find or create hospital by name
        if (hospitalId == null) {
            Hospital existing = hospitalRepository.findByName(hospitalName).orElse(null);
            if (existing == null) {
                Hospital created = hospitalRepository.save(Hospital.builder().name(hospitalName).build());
                hospitalId = created.getId();
            } else {
                hospitalId = existing.getId();
            }
        } else {
            // verify hospital exists
            boolean exists = hospitalRepository.findById(hospitalId).isPresent();
            if (!exists) {
                throw new BadCredentialsException("Hospital not found");
            }
        }

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .hospitalId(hospitalId)
                .build();

        userRepository.save(user);
        return "User Registered Successfully!";
    }

    // Register a user into the authenticated ADMIN's hospital
    public String registerForAdminHospital(String adminUsername, RegisterUserPerHospitalDTO dto) {
        // Resolve admin by username+current tenant hospitalId to avoid duplicate usernames across hospitals
        Long currentHospitalId = TenantContext.getHospitalId();
        if (currentHospitalId == null) {
            throw new BadCredentialsException("No hospital context available");
        }

        User admin = userRepository.findByUsernameAndHospitalId(adminUsername, currentHospitalId)
                .orElseThrow(() -> new BadCredentialsException("Admin user not found in current hospital"));
        if (admin.getRole() != Role.ADMIN) {
            throw new BadCredentialsException("Only ADMIN can register users for their hospital");
        }

        Long hospitalId = admin.getHospitalId();
        if (hospitalId == null) {
            throw new BadCredentialsException("Admin has no hospital assigned");
        }

        // verify hospital exists
        if (hospitalRepository.findById(hospitalId).isEmpty()) {
            throw new BadCredentialsException("Hospital not found");
        }

        // Prevent duplicate usernames within the same hospital
        if (userRepository.findByUsernameAndHospitalId(dto.getUsername(), hospitalId).isPresent()) {
            throw new BadCredentialsException("Username already exists in this hospital");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .hospitalId(hospitalId)
                .build();

        userRepository.save(user);
        return "User Registered Successfully!";
    }

    public AuthResponseDTO login(AuthRequestDTO dto) {
        // Try to find a persisted user first
        User user = userRepository.findByUsername(dto.getUsername()).orElse(null);

        // If user not found, check if credentials match the configured super-admin
        if (user == null) {
            if (configuredSuperAdminUsername != null && configuredSuperAdminUsername.equals(dto.getUsername())
                    && configuredSuperAdminPasswordHash != null
                    && passwordEncoder.matches(dto.getPassword(), configuredSuperAdminPasswordHash)) {
                // Create a transient super-admin user for token generation
                User superUser = User.builder()
                        .username(configuredSuperAdminUsername)
                        .password(configuredSuperAdminPasswordHash)
                        .role(Role.SUPER_ADMIN)
                        .hospitalId(null)
                        .build();

                String token = jwtService.generateToken(superUser);
                return new AuthResponseDTO(token, superUser.getRole().name(),"");
            }

            throw new BadCredentialsException("User not found");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        String token = jwtService.generateToken(user);
        Hospital hospital = hospitalRepository
                .findById(user.getHospitalId())
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        String hospitalName = hospital.getName();
        return new AuthResponseDTO(token, user.getRole().name(), hospitalName);
    }
}
