package com.prasad.incidentanalyzer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.prasad.incidentanalyzer.constant.ApiPath;
import com.prasad.incidentanalyzer.dto.IncidentSummaryResponse;
import com.prasad.incidentanalyzer.dto.IncidentUploadResponse;
import com.prasad.incidentanalyzer.service.IncidentService;


@RestController
@RequestMapping(ApiPath.INCIDENT_BASE)
public class IncidentController {

    @Autowired
    private IncidentService incidentService;

    /**
     * Upload log files and create a new incident
     */
    @PostMapping(ApiPath.INCIDENT_UPLOAD)
    public IncidentUploadResponse uploadIncidentLogs(
            @RequestParam("logs") List<MultipartFile> logs,
            @RequestParam(value = "title", required = false) String title
    ) {
        return incidentService.createIncident(logs, title);
    }
      
    /**
     * Get all incidents
     */
    
    @GetMapping(ApiPath.INCIDENT_GET_ALL)
    public List<IncidentSummaryResponse> getAllIncidents() {
        return incidentService.getAllIncidents();
    }
    
    /**
     * Get incident by ID
     */
   
    @GetMapping(ApiPath.INCIDENT_GET_BY_ID)
    public IncidentSummaryResponse getIncidentById(
            @PathVariable String incidentId
    ) {
        return incidentService.getIncident(incidentId);
    }
    
   
}
