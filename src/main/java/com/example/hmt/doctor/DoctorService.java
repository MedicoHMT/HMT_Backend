package com.example.hmt.doctor;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {
    @Autowired
    private DoctorRepository doctorRepository;

    public List<DoctorResponseDTO> getAllDoctors() {

        return doctorRepository
                .findAll()
                .stream()
                .map(DoctorMapper::toDoctorResponseDto)
                .collect(Collectors.toList());
    }

}
