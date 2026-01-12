package com.prasad.incidentanalyzer.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class IncidentSummaryResponse {

    private String incidentId;
    private String title;
    private String status;
    private LocalDateTime createdAt;
}