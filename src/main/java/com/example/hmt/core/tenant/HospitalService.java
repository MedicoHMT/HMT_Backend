package com.example.hmt.core.tenant;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HospitalService {
    private final HospitalRepository hospitalRepository;

    public Hospital create(Hospital hospital) {
        if (hospitalRepository.existsByName(hospital.getName())) {
            throw new IllegalArgumentException("Hospital with name '" + hospital.getName() + "' already exists");
        }

        if (hospitalRepository.existsByHospitalCode(hospital.getHospitalCode())) {
            throw new IllegalArgumentException("Hospital code '" + hospital.getHospitalCode() + "' is already in use");
        }

        return hospitalRepository.save(hospital);
    }

    public List<Hospital> listAll() {
        return hospitalRepository.findAll();
    }

    @Transactional
    public Hospital update(Long id, Hospital request) {

        Hospital existing = hospitalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hospital not found with id: " + id));

        if (request.getName() != null
                && !request.getName().equals(existing.getName())
                && hospitalRepository.existsByName(request.getName())) {

            throw new IllegalArgumentException("Hospital name '" + request.getName() + "' is already taken");
        }

        if (request.getName() != null) {
            existing.setName(request.getName());
        }
        if (request.getAddress() != null) {
            existing.setAddress(request.getAddress());
        }
        return existing;
    }

    public void delete(Long id) {
        hospitalRepository.deleteById(id);
    }
}

