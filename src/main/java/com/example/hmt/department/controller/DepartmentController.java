package com.example.hmt.department.controller;

import com.example.hmt.department.dto.DepartmentRequestDTO;
import com.example.hmt.department.dto.DepartmentResponseDTO;
import com.example.hmt.department.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/departments")
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    public List<DepartmentResponseDTO> getAllDepartments() {
        return departmentService.getAllDepartment();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<DepartmentResponseDTO> createDepartment(@Valid @RequestBody DepartmentRequestDTO dto) {
        DepartmentResponseDTO response = departmentService.createDepartmentForHospital(dto);
        return ResponseEntity.status(201).body(response);
    }
}
