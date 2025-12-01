package com.example.hmt.core.auth.repository;

import com.example.hmt.core.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String emai);

    Optional<User> findByUsernameAndHospitalId(String username, Long hospitalId);

    boolean existsByUsernameAndHospitalId(String username, Long hospitalId);
}
