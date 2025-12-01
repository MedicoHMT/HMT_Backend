package com.example.hmt.core.tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    boolean existsByName(String name);

    boolean existsByHospitalCode(String hospitalCode);
}

