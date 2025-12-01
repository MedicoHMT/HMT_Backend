package com.example.hmt.core.config;

import com.example.hmt.core.auth.model.User;
import com.example.hmt.core.auth.repository.UserRepository;
import com.example.hmt.core.auth.service.RefreshTokenService;
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
    private final RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        String jwt = null;
        String username = null;
        Long hospitalId = null;

        try {

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
                    // NEW: check access token revocation
                    String accessJti = jwtService.extractJti(jwt);
                    if (accessJti != null && refreshTokenService.isRevokedAccessJti(accessJti)) {
                        // token has been revoked -> do NOT authenticate
                        chain.doFilter(request, response);
                        return;
                    }


                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            null,
                            user.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {

                    // User not found in DB or DB-based validation failed.
                    // Validate token signature/expiration and build auth from token claims.
                    if (jwtService.isTokenValid(jwt)) {
                        String roleClaim = jwtService.extractRole(jwt);
                        if (roleClaim != null) {
                            // Add "ROLE_" prefix to maintain consistency with hasRole()
                            String authority = roleClaim.startsWith("ROLE_") ? roleClaim : "ROLE_" + roleClaim;
                            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    List.of(new SimpleGrantedAuthority(authority))
                            );
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                    }
                }
            }

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // CATCH THE EXPIRED ERROR
            // The SecurityContext remains empty (Unauthenticated).
            // This allows the request to reach "/auth/superadmin/refresh" successfully.
            System.out.println("JWT Expired, proceeding as anonymous user for refresh: " + e.getMessage());

        } catch (Exception e) {
            // Handle malformed JWTs or other parsing errors
            System.out.println("JWT Error: " + e.getMessage());
        }

        try {
            chain.doFilter(request, response);
        } finally {
            // Clear tenant context to avoid leaking between requests
            TenantContext.clear();
        }
    }
}
