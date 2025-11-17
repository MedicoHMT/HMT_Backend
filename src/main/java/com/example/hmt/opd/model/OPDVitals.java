package com.example.hmt.opd.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "opd_vitals")
@Data
public class OPDVitals {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "opd_id", referencedColumnName = "opd_id", nullable = false, unique = true)
    private OPDVisit opdVisit;

    private Integer pulse;
    private Integer spo2;
    private String bp;
    private Double temperature;
    private Integer respiration;
    private Double weight;
    private Double height;



    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;

}
