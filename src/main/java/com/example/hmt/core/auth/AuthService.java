package com.example.hmt.core.auth;

import com.example.hmt.core.auth.dto.AuthRequestDTO;
import com.example.hmt.core.auth.dto.AuthResponseDTO;
import com.example.hmt.core.auth.dto.RegisterUserDTO;
import com.example.hmt.core.auth.model.User;
import com.example.hmt.core.auth.repository.UserRepository;
import com.example.hmt.core.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public String register(RegisterUserDTO dto) {
        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .build();

        userRepository.save(user);
        return "User Registered Successfully!";
    }

    public AuthResponseDTO login(AuthRequestDTO dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponseDTO(token, user.getRole().name());
    }
}
