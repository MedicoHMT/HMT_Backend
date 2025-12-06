package com.example.hmt.department.repository;

import com.example.hmt.department.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository  extends JpaRepository<Department,Long> {
    List<Department> findAllByHospitalId(Long hospitalId);

    boolean existsByCodeAndHospitalId(String code, Long hospitalId);

    boolean existsByNameAndHospitalId(String name, Long hospitalId);
}
