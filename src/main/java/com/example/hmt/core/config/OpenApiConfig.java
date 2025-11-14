package com.example.hmt.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;

@Configuration
public class OpenApiConfig {

    // 🔷 Global Swagger Info
    @Bean
    public OpenAPI baseOpenAPI() {
        // Define security scheme name
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .info(new Info()
                        .title("Hospital Management System API")
                        .version("1.0")
                        .description("API Documentation for HMS – OPD, IPD, Appointments, Auth, Doctor, Patient")
                        .contact(new Contact().name("Priyanshu Narwaria")));
    }
    // 🔹 Auth APIs → /api/auth/**
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("Authentication")
                .pathsToMatch("/api/auth/**")
                .build();
    }

    // 🔹 Patient APIs → /api/patients/**
    @Bean
    public GroupedOpenApi patientApi() {
        return GroupedOpenApi.builder()
                .group("Patient")
                .pathsToMatch("/api/v1/patients/**")
                .build();
    }

    // 🔹 Doctor APIs → /api/doctors/**
    @Bean
    public GroupedOpenApi doctorApi() {
        return GroupedOpenApi.builder()
                .group("Doctor")
                .pathsToMatch("/api/v1/doctors/**")
                .build();
    }

    // 🔹 Appointment → /api/appointments/**
    @Bean
    public GroupedOpenApi appointmentApi() {
        return GroupedOpenApi.builder()
                .group("Appointment")
                .pathsToMatch("/api/v1/appointments/**")
                .build();
    }

    // 🔹 OPD Module → opd folder
    @Bean
    public GroupedOpenApi opdApi() {
        return GroupedOpenApi.builder()
                .group("OPD")
                .pathsToMatch(
                        "/api/v1/opd/visits/**",
                        "/api/v1/opd/vitals/**",
                        "/api/v1/opd/assessment/**",
                        "/api/v1/opd/prescriptions/**",
                        "/api/v1/opd/queue/**",
                        "/api/v1/opd/investigations/**",
                        "/api/v1/opd/followup/**"
                )
                .build();
    }

    // 🔹 IPD Module → ipd folder
    @Bean
    public GroupedOpenApi ipdApi() {
        return GroupedOpenApi.builder()
                .group("IPD")
                .pathsToMatch("/api/ipd/**")
                .build();
    }

    // 🔹 Admin → Role / Permission / User mgmt
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("Admin")
                .pathsToMatch("/api/v1/admin/**")
                .build();
    }

    // 🔹 Hospital / Tenant APIs (Super Admin)
    @Bean
    public GroupedOpenApi hospitalApi() {
        return GroupedOpenApi.builder()
                .group("Hospital Management")
                .pathsToMatch("/api/v1/hospitals/**")
                .build();
    }
}