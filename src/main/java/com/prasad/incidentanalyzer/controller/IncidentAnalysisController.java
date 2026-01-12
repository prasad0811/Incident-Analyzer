package com.prasad.incidentanalyzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prasad.incidentanalyzer.constant.ApiPath;
import com.prasad.incidentanalyzer.dto.IncidentReportResponse;
import com.prasad.incidentanalyzer.service.IncidentAnalysisService;

@RestController
@RequestMapping(ApiPath.AI_BASE)
public class IncidentAnalysisController {

    @Autowired
    private IncidentAnalysisService incidentAnalysisService;

    
    @PostMapping(ApiPath.AI_ANALYZE)
    public IncidentReportResponse analyzeIncident(
            @PathVariable String incidentId
    ) {
        return incidentAnalysisService.analyzeIncident(incidentId);
    }


    @GetMapping(ApiPath.AI_REPORT)
    public IncidentReportResponse getIncidentReport(
            @PathVariable String incidentId
    ) {
        return incidentAnalysisService.getReport(incidentId);
    }

}
