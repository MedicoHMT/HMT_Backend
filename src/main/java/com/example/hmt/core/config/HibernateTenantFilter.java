package com.example.hmt.core.config;

import com.example.hmt.core.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


public class HibernateTenantFilter extends OncePerRequestFilter {

    private final EntityManager entityManager;

    public HibernateTenantFilter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Long hospitalId = TenantContext.getHospitalId();
        Session hibernateSession = null;
        boolean filterEnabled = false;

        try {
            if (hospitalId != null) {
                hibernateSession = entityManager.unwrap(org.hibernate.Session.class);
                // enable filter and set parameter
                hibernateSession.enableFilter("tenantFilter")
                        .setParameter("hospitalId", hospitalId);
                filterEnabled = true;
            }

            filterChain.doFilter(request, response);
        } finally {
            // we don't leak to next request/session
            if (filterEnabled && hibernateSession != null) {
                try {
//                    hibernateSession.disableFilter("tenantFilter");
                } catch (Exception ignored) {
                }
            }

        }
    }
}