package com.example.hmt.core.auth;

import com.example.hmt.core.auth.dto.AuthRequestDTO;
import com.example.hmt.core.auth.dto.AuthResponseDTO;
import com.example.hmt.core.auth.dto.RegisterUserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {


    private final AuthService authService;

    @PostMapping("/api/auth/register")
    public ResponseEntity<String> register(@RequestBody RegisterUserDTO dto) {
        return ResponseEntity.ok(authService.register(dto));
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    // ADMINs can register users for their own hospital
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/api/v1/admin/register")
    public ResponseEntity<String> registerForAdminHospital(@RequestBody RegisterUserDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminUsername = authentication.getName();
        return ResponseEntity.ok(authService.registerForAdminHospital(adminUsername, dto));
    }
}
