package com.example.hmt.core.tenant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HospitalService {
    private final HospitalRepository hospitalRepository;

    public Hospital create(Hospital hospital) {
        if (hospitalRepository.existsByName(hospital.getName())) {
            throw new IllegalArgumentException("Hospital with this name already exists");
        }
        return hospitalRepository.save(hospital);
    }

    public List<Hospital> listAll() {
        return hospitalRepository.findAll();
    }

    public Hospital findById(Long id) {
        return hospitalRepository.findById(id).orElse(null);
    }

    public Hospital update(Long id, Hospital updated) {
        return hospitalRepository.findById(id).map(h -> {
            h.setName(updated.getName());
            h.setAddress(updated.getAddress());
            return hospitalRepository.save(h);
        }).orElseThrow(() -> new IllegalArgumentException("Hospital not found"));
    }

    public void delete(Long id) {
        hospitalRepository.deleteById(id);
    }
}

