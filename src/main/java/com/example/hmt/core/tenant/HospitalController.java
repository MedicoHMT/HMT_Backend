package com.example.hmt.core.tenant;

import com.example.hmt.core.tenant.dto.HospitalDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    // Only SUPER_ADMIN can manage hospitals
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<HospitalDTO> create(@RequestBody HospitalDTO dto) {
        Hospital inputEntity = dto.toEntity();

        Hospital createdEntity = hospitalService.create(inputEntity);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(HospitalDTO.fromEntity(createdEntity));
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @GetMapping
    public List<HospitalDTO> list() {
        return hospitalService.listAll().stream().map(h -> {
            HospitalDTO d = new HospitalDTO();
            d.setName(h.getName());
            d.setAddress(h.getAddress());
            return d;
        }).collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<HospitalDTO> update(@PathVariable Long id, @RequestBody HospitalDTO dto) {
        Hospital updateRequest = Hospital.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .build();

        Hospital updatedEntity = hospitalService.update(id, updateRequest);

        return ResponseEntity.ok(HospitalDTO.fromEntity(updatedEntity));
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        hospitalService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

