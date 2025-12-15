package com.example.hmt.opd.service;

import com.example.hmt.core.auth.model.User;
import com.example.hmt.core.auth.repository.UserRepository;
import com.example.hmt.core.handler.exception.ResourceNotFoundException;
import com.example.hmt.opd.dto.request.OPDVitalRequestDTO;
import com.example.hmt.opd.dto.response.OPDVitalResponseDTO;
import com.example.hmt.opd.mapper.OPDVitalMapper;
import com.example.hmt.opd.model.OPDVisit;
import com.example.hmt.opd.model.OPDVitals;
import com.example.hmt.opd.repository.OPDVisitRepository;
import com.example.hmt.opd.repository.OPDVitalsRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class OPDVitalsService {
    private final OPDVitalsRepository vitalsRepository;
    private final OPDVisitRepository visitRepository;
    private final UserRepository userRepository;

    public OPDVitalsService(
            OPDVitalsRepository vitalsRepository,
            OPDVisitRepository visitRepository,
            UserRepository userRepository) {
        this.vitalsRepository = vitalsRepository;
        this.visitRepository = visitRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public String createVitals(OPDVitalRequestDTO vitalsDTO, Long hospitalId) {
        OPDVisit opdVisit = visitRepository.findByOpdVisitIdAndHospital_Id(
                        vitalsDTO.getOpdVisitId(), hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException("OPD Visit Not Found"));

        User user = userRepository.findByUsername(
                        SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not authenticated properly"));

        OPDVitals vitals = OPDVitals.builder()
                .opdVisit(opdVisit)
                .pulseRate(vitalsDTO.getPulseRate())
                .bpSystolic(vitalsDTO.getBpSystolic())
                .bpDiastolic(vitalsDTO.getBpDiastolic())
                .temperature(vitalsDTO.getTemperature())
                .spo2(vitalsDTO.getSpo2())
                .respirationRate(vitalsDTO.getRespirationRate())
                .weight(vitalsDTO.getWeight())
                .height(vitalsDTO.getHeight())
                .recordedAt(Instant.now())
                .recordedBy(user)
                .build();

        vitalsRepository.save(vitals);

        return "OPD Vitals Saved Successfully";
    }

    public OPDVitalResponseDTO getOPDVitals(String opdVisitId, Long hospitalId) {
        return vitalsRepository.findByOpdVisit_OpdVisitIdAndHospital_Id(opdVisitId, hospitalId)
                .map(vital -> OPDVitalMapper.mapToOPDVitalResponseDTO(vital, true))
                .orElseThrow(() -> new ResourceNotFoundException("OPD Visit Not Found"));
    }
}
