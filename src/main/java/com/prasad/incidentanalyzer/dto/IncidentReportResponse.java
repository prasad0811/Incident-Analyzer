package com.prasad.incidentanalyzer.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class IncidentReportResponse {

    private String incidentId;
    private String status;
    private String summary;

    private Map<String, Object> observations;

    private List<String> suspected_root_causes;
    private List<String> recommendations;

    private String model_used;
    private LocalDateTime generated_at;
}
