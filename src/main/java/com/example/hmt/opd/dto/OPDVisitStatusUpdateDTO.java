package com.example.hmt.opd.dto;

import lombok.Data;

@Data
public class OPDVisitStatusUpdateDTO {
    private String status;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}