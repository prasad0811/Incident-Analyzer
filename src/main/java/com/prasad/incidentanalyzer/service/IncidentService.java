package com.prasad.incidentanalyzer.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.prasad.incidentanalyzer.dto.IncidentSummaryResponse;
import com.prasad.incidentanalyzer.dto.IncidentUploadResponse;

public interface IncidentService {

	 /**
     *
     * @param logs  uploaded log files
     * @param title optional incident title
     * @return IncidentUploadResponse
     */
    IncidentUploadResponse createIncident(List<MultipartFile> logs, String title);

	List<IncidentSummaryResponse> getAllIncidents();

	IncidentSummaryResponse getIncident(String incidentId);
}
