package com.example.hmt.doctor;

import com.example.hmt.department.DepartmentMapper;

public class DoctorMapper {

    public static DoctorResponseDTO toDoctorResponseDto(Doctor doctor) {
        return DoctorResponseDTO.builder()
                .doctorId(doctor.getDoctorId())
                .firstName(doctor.getUser().getName().getFirstName())
                .middleName(doctor.getUser().getName().getMiddleName())
                .lastName(doctor.getUser().getName().getLastName())
                .specialization(doctor.getSpecialization())
                .consultationFee(doctor.getConsultation_fee())
                .department(DepartmentMapper.toDepartmentRequestDTO(doctor.getDepartment()))
                .build();
    }
}