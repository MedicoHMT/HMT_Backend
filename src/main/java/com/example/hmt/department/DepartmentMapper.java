package com.example.hmt.department;

import com.example.hmt.department.dto.DepartmentResponseDTO;
import com.example.hmt.department.model.Department;

public class DepartmentMapper {
    public static DepartmentResponseDTO toDepartmentRequestDTO(Department department) {
        return DepartmentResponseDTO.builder()
                .department_id(department.getDepartment_id())
                .code(department.getCode())
                .name(department.getName())
                .description(department.getDescription())
                .build();
    }
}
