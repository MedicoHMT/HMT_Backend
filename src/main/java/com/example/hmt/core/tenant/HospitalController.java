package com.example.hmt.core.tenant;

import com.example.hmt.core.tenant.dto.HospitalDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
        Hospital h = Hospital.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .build();
        Hospital created = hospitalService.create(h);
        HospitalDTO out = new HospitalDTO();
        out.setId(created.getId());
        out.setName(created.getName());
        out.setAddress(created.getAddress());
        return ResponseEntity.created(URI.create("/api/v1/hospitals/" + created.getId())).body(out);
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @GetMapping
    public List<HospitalDTO> list() {
        return hospitalService.listAll().stream().map(h -> {
            HospitalDTO d = new HospitalDTO();
            d.setId(h.getId());
            d.setName(h.getName());
            d.setAddress(h.getAddress());
            return d;
        }).collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<HospitalDTO> update(@PathVariable Long id, @RequestBody HospitalDTO dto) {
        Hospital updated = hospitalService.update(id, Hospital.builder().name(dto.getName()).address(dto.getAddress()).build());
        HospitalDTO out = new HospitalDTO();
        out.setId(updated.getId());
        out.setName(updated.getName());
        out.setAddress(updated.getAddress());
        return ResponseEntity.ok(out);
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        hospitalService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

