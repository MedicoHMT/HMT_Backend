package com.example.hmt.ipd;

import com.example.hmt.core.enums.AdmissionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IPDAdmissionService {

    private final IPDAdmissionRepository ipdAdmissionRepository;

    public IPDAdmission admitPatient(IPDAdmission admission) {
        admission.setAdmissionDate(LocalDate.now());
        admission.setStatus(AdmissionStatus.ADMITTED);
        return ipdAdmissionRepository.save(admission);
    }

    public IPDAdmission dischargePatient(Long id, String notes) {
        IPDAdmission admission = ipdAdmissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admission not found"));
        admission.setDischargeDate(LocalDate.now());
        admission.setStatus(AdmissionStatus.DISCHARGED);
        admission.setTreatmentNotes(notes);
        return ipdAdmissionRepository.save(admission);
    }

    public List<IPDAdmission> getAllAdmissions() {
        return ipdAdmissionRepository.findAll();
    }

    public List<IPDAdmission> getAdmissionsByPatient(Long patientId) {
        return ipdAdmissionRepository.findByPatientId(patientId);
    }

    public List<IPDAdmission> getAdmissionsByDoctor(Long doctorId) {
        return ipdAdmissionRepository.findByDoctorId(doctorId);
    }
}
