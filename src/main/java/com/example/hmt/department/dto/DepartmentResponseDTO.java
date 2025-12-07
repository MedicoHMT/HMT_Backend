package com.example.hmt.department.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepartmentResponseDTO {
    private String code;
    private String name;
    private String description;
    private Long department_id;
}
