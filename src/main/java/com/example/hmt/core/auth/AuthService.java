package com.example.hmt.core.auth;

import com.example.hmt.core.auth.dto.RegisterUserDTO;
import com.example.hmt.core.auth.model.Role;
import com.example.hmt.core.auth.model.User;
import com.example.hmt.core.auth.repository.UserRepository;
import com.example.hmt.core.tenant.Hospital;
import com.example.hmt.core.tenant.HospitalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
                .build();

        userRepository.save(user);
    }


//    // Register a user into the authenticated ADMIN's hospital
//    public String registerForAdminHospital(String adminUsername, RegisterUserPerHospitalDTO dto) {
//        // Resolve admin by username+current tenant hospitalId to avoid duplicate usernames across hospitals
//        Long currentHospitalId = TenantContext.getHospitalId();
//        if (currentHospitalId == null) {
//            throw new BadCredentialsException("No hospital context available");
//        }
//
//        User admin = userRepository.findByUsernameAndHospitalId(adminUsername, currentHospitalId).orElseThrow(() -> new BadCredentialsException("Admin user not found in current hospital"));
//        if (admin.getRole() != Role.ADMIN) {
//            throw new BadCredentialsException("Only ADMIN can register users for their hospital");
//        }
//
//        Long hospitalId = admin.getHospitalId();
//        if (hospitalId == null) {
//            throw new BadCredentialsException("Admin has no hospital assigned");
//        }
//
//        // verify hospital exists
//        if (hospitalRepository.findById(hospitalId).isEmpty()) {
//            throw new BadCredentialsException("Hospital not found");
//        }
//
//        // Prevent duplicate usernames within the same hospital
//        if (userRepository.findByUsernameAndHospitalId(dto.getUsername(), hospitalId).isPresent()) {
//            throw new BadCredentialsException("Username already exists in this hospital");
//        }
//
//        User user = User.builder().username(dto.getUsername()).password(passwordEncoder.encode(dto.getPassword())).role(dto.getRole()).hospitalId(hospitalId).build();
//
//        userRepository.save(user);
//        return "User Registered Successfully!";
//    }
//

}
