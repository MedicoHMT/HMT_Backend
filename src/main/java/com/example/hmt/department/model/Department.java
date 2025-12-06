package com.example.hmt.department.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long department_id;

    @NotNull
    private String name;


    @NotNull
    private String code;


    private String description;

    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;

}
