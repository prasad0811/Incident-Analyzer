package com.prasad.incidentanalyzer.service;

import com.prasad.incidentanalyzer.dto.IncidentReportResponse;

public interface IncidentAnalysisService {
	/**
	 * Triggers AI analysis for a given incident. Reads logs from incident folder,
	 * sends them to Ollama, stores the generated report in DB.
	 *
	 * @param incidentId unique incident identifier
	 * @return IncidentReportResponse
	 */
	IncidentReportResponse analyzeIncident(String incidentId);

	/**
	 * Fetches already generated AI report for an incident.
	 *
	 * @param incidentId unique incident identifier
	 * @return IncidentReportResponse
	 */
	IncidentReportResponse getReport(String incidentId);

}
