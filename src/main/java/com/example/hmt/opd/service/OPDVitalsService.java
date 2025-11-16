package com.example.hmt.opd.service;

import com.example.hmt.core.tenant.TenantContext;
import com.example.hmt.opd.dto.OPDVitalsDTO;
import com.example.hmt.opd.model.OPDVisit;
import com.example.hmt.opd.model.OPDVitals;
import com.example.hmt.opd.repository.OPDVisitRepository;
import com.example.hmt.opd.repository.OPDVitalsRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OPDVitalsService {

    private final OPDVitalsRepository vitalsRepo;
    private final OPDVisitRepository visitRepo;

    public OPDVitalsService(OPDVitalsRepository vitalsRepo, OPDVisitRepository visitRepo) {
        this.vitalsRepo = vitalsRepo;
        this.visitRepo = visitRepo;
    }

    public OPDVitals saveVitals(OPDVitalsDTO dto) {

        Long hospitalId = TenantContext.getHospitalId();

        OPDVisit visit = visitRepo.findByIdAndHospitalId(dto.getVisitId(), hospitalId)
                .orElseThrow(() -> new RuntimeException("OPD Visit not found"));

        OPDVitals vitals = vitalsRepo.findByVisitIdAndHospitalId(dto.getVisitId(), hospitalId)
                .orElse(new OPDVitals());

        vitals.setHospitalId(hospitalId);
        vitals.setVisit(visit);

        vitals.setPulse(dto.getPulse());
        vitals.setSpo2(dto.getSpo2());
        vitals.setBp(dto.getBp());
        vitals.setTemperature(dto.getTemperature());
        vitals.setRespiration(dto.getRespiration());
        vitals.setWeight(dto.getWeight());
        vitals.setHeight(dto.getHeight());

        return vitalsRepo.save(vitals);
    }

    public Optional<OPDVitals> getVitals(Long visitId) {
        Long hospitalId = TenantContext.getHospitalId();
        return vitalsRepo.findByVisitIdAndHospitalId(visitId, hospitalId);
    }

}
