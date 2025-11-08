package com.example.hmt.doctor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {
    @Autowired
    private DoctorRepository doctorRepository;

    public List<DoctorModel> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Optional<DoctorModel> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    public DoctorModel createDoctor(DoctorModel doctor) {
        return doctorRepository.save(doctor);
    }

    public DoctorModel updateDoctor(Long id, DoctorModel doctorDetails) {
        DoctorModel doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));

        doctor.setFirstName(doctorDetails.getFirstName());
        doctor.setLastName(doctorDetails.getLastName());
        doctor.setSpecialization(doctorDetails.getSpecialization());
        doctor.setContactNumber(doctorDetails.getContactNumber());

        return doctorRepository.save(doctor);
    }

    public void deleteDoctor(Long id) {
        DoctorModel doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));

        doctorRepository.delete(doctor);
    }
}
