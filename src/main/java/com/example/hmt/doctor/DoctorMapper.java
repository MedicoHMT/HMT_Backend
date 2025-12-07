package com.example.hmt.doctor;

import com.example.hmt.department.DepartmentMapper;

public class DoctorMapper {

    public static DoctorResponseDTO toDoctorResponseDto(Doctor doctor) {
        return DoctorResponseDTO.builder()
                .doctorId(doctor.getDoctorId())
                .firstName(doctor.getUser().getFirstName())
                .lastName(doctor.getUser().getLastName())
                .specialization(doctor.getSpecialization())
                .consultationFee(doctor.getConsultation_fee())
                .department(DepartmentMapper.toDepartmentRequestDTO(doctor.getDepartment()))
                .build();
    }
}