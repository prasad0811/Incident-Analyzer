package com.prasad.incidentanalyzer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prasad.incidentanalyzer.constant.ApiPath;

@RestController
@RequestMapping(ApiPath.INCIDENT_BASE)
public class HealthController {
	
	@GetMapping(ApiPath.HEALTH_CHECK)
    public String health() {
        return "Incident Analyzer is running";
    }

}
