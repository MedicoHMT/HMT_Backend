package com.example.hmt.core.auth.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "super_admins")
@Data
public class SuperAdmin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String email;

    @Column(name="created_at", nullable=false)
    private Instant createdAt = Instant.now();

    @Column(name="last_login")
    private Instant lastLogin;


    public SuperAdmin() {}
    public SuperAdmin(String email) { this.email = email; }

}
