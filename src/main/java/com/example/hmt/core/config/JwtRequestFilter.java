package com.example.hmt.core.config;

import com.example.hmt.core.auth.model.User;
import com.example.hmt.core.auth.repository.UserRepository;
import com.example.hmt.core.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        String username = null;
        String jwt = null;
        Long hospitalId = null;

        if (header != null && header.startsWith("Bearer ")) {
            jwt = header.substring(7);
            username = jwtService.extractUsername(jwt);
            hospitalId = jwtService.extractHospitalId(jwt);
        }

        if (hospitalId != null) {
            TenantContext.setHospitalId(hospitalId);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null && jwtService.isTokenValid(jwt, user)) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                user.getUsername(), null,
                                List.of(new SimpleGrantedAuthority(user.getRole().name()))
                        );
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                // User not found in DB or DB-based validation failed.
                // Validate token signature/expiration and build auth from token claims.
                if (jwt != null && jwtService.isTokenValid(jwt)) {
                    String roleClaim = jwtService.extractRole(jwt);
                    if (roleClaim != null) {
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        username, null,
                                        List.of(new SimpleGrantedAuthority(roleClaim))
                                );
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            }
        }

        try {
            chain.doFilter(request, response);
        } finally {
            // Clear tenant context to avoid leaking between requests
            TenantContext.clear();
        }
    }
}
