package com.prasad.incidentanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IncidentUploadResponse {

    private String incidentId;
    private String status;
    private String message;
}