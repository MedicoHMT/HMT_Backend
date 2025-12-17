package com.example.hmt.core.auth.model;

import com.example.hmt.core.tenant.Hospital;
import com.example.hmt.core.tenant.Name;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "id", length = 36, updatable = false, nullable = false)
    private UUID id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "hospital_id",
            foreignKey = @ForeignKey(name = "fk_user_hospital")
    )
    private Hospital hospital;


    @Column(unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    private Name name;

    @Column(name = "phone_number")
    private Long phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;


    @ElementCollection(targetClass = UserPermission.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_permissions", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "permission")
    @Builder.Default
    /// Prevents Builder from overwriting initialization with null
    private Set<UserPermission> permissions = new HashSet<>();


    // ---------- Timestamps ----------
    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @Column(name = "last_login")
    private Instant lastLogin;


    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        //  Add the base Role (e.g., ROLE_ADMIN)
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.role.name()));

        // Add the dynamic Permissions (e.g., PATIENT_READ)
        // If user is ADMIN, you might grant ALL permissions automatically here

        if (this.role == Role.ADMIN) {
            // grant everything
            for (UserPermission p : UserPermission.values()) {
                authorities.add(new SimpleGrantedAuthority(p.name()));
            }
        } else {
            for (UserPermission p : permissions) {
                authorities.add(new SimpleGrantedAuthority(p.name()));
            }
        }

        return authorities;
    }
}
