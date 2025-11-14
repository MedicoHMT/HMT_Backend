package com.example.hmt.opd.service;


import com.example.hmt.opd.model.OPDVisit;
import com.example.hmt.core.enums.VisitStatus;
import com.example.hmt.opd.repository.OPDVisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OPDVisitService {
    private final OPDVisitRepository opdVisitRepository;

    public OPDVisit createVisit(OPDVisit opdVisit) {
        opdVisit.setVisitDate(LocalDate.now());
        return opdVisitRepository.save(opdVisit);
    }

    public List<OPDVisit> getAllVisits() {
        return opdVisitRepository.findAll();
    }

    public List<OPDVisit> getVisitsByDoctor(Long doctorId) {
        return opdVisitRepository.findByDoctorId(doctorId);
    }

    public List<OPDVisit> getVisitsByPatient(Long patientId) {
        return opdVisitRepository.findByPatientId(patientId);
    }

    public OPDVisit updateStatus(Long id, VisitStatus status) {
        OPDVisit visit = opdVisitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visit not found"));
        visit.setStatus(status);
        return opdVisitRepository.save(visit);
    }
}
