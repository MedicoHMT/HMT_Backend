package com.example.hmt.department.dto;

import lombok.Data;

@Data
public class DepartmentResponseDTO {
    private String code;
    private String name;
    private String description;
    private Long department_id;
}
