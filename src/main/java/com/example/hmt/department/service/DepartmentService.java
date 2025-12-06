package com.example.hmt.department.service;

import com.example.hmt.core.auth.repository.UserRepository;
import com.example.hmt.core.tenant.TenantContext;
import com.example.hmt.department.dto.DepartmentRequestDTO;
import com.example.hmt.department.dto.DepartmentResponseDTO;
import com.example.hmt.department.model.Department;
import com.example.hmt.department.repository.DepartmentRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    @Transactional
    public DepartmentResponseDTO createDepartmentForHospital(DepartmentRequestDTO dto) {
        Long currentHospitalId = TenantContext.getHospitalId();
        if (currentHospitalId == null) {
            throw new IllegalStateException("No hospital context found. Cannot create department.");
        }


        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Department name is required.");
        }
        if (dto.getCode() == null || dto.getCode().isBlank()) {
            throw new IllegalArgumentException("Department code is required.");
        }

//        duplicate checks
        if (departmentRepository.existsByCodeAndHospitalId(dto.getCode(), currentHospitalId)) {
            throw new IllegalArgumentException("Department code '" + dto.getCode() + "' already exists in this hospital.");
        }
        if (departmentRepository.existsByNameAndHospitalId(dto.getName(), currentHospitalId)) {
            throw new IllegalArgumentException("Department name '" + dto.getName() + "' already exists in this hospital.");
        }

        Department dept = Department
                .builder()
                .name(dto.getName().trim())
                .code(dto.getCode().trim())
                .description(dto.getDescription())
                .hospitalId(currentHospitalId)
                .build();

        dept = departmentRepository.save(dept);


        DepartmentResponseDTO resp = new DepartmentResponseDTO();
        resp.setDepartment_id(dept.getDepartment_id());
        resp.setCode(dept.getCode());
        resp.setName(dept.getName());
        resp.setDescription(dept.getDescription());
        return resp;
    }

    public List<DepartmentResponseDTO> getAllDepartment() {
        Long hospitalId = TenantContext.getHospitalId();
        List<Department> departments = departmentRepository.findAllByHospitalId(hospitalId);
        return departments.stream()
                .map(d -> {
                    DepartmentResponseDTO dto = new DepartmentResponseDTO();
                    dto.setDepartment_id(d.getDepartment_id());
                    dto.setName(d.getName());
                    dto.setDescription(d.getDescription());
                    dto.setCode(d.getCode());
                    return dto;
                })
                .collect(Collectors.toList());
    }

}
