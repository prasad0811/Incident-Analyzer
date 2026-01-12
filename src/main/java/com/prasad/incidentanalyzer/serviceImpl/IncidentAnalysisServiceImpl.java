package com.prasad.incidentanalyzer.serviceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prasad.incidentanalyzer.constant.Constant;
import com.prasad.incidentanalyzer.dto.IncidentReportResponse;
import com.prasad.incidentanalyzer.entity.AppConfigEntity;
import com.prasad.incidentanalyzer.entity.IncidentAnalysisEntity;
import com.prasad.incidentanalyzer.entity.IncidentEntity;
import com.prasad.incidentanalyzer.repository.AppConfigRepository;
import com.prasad.incidentanalyzer.repository.IncidentAnalysisRepository;
import com.prasad.incidentanalyzer.repository.IncidentRepository;
import com.prasad.incidentanalyzer.service.IncidentAnalysisService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class IncidentAnalysisServiceImpl implements IncidentAnalysisService {

	private static final Logger log = LoggerFactory.getLogger(IncidentAnalysisServiceImpl.class);

	private final IncidentRepository incidentRepository;
	private final IncidentAnalysisRepository analysisRepository;
	private final AppConfigRepository appConfigRepository;
	private final RestTemplate restTemplate;

	@Value("${ollama.api.url}")
	private String ollamaApiUrl;

	@Value("${ollama.model.name}")
	private String modelName;
	
	private final ObjectMapper objectMapper = new ObjectMapper();


	public IncidentAnalysisServiceImpl(IncidentRepository incidentRepository,
			IncidentAnalysisRepository analysisRepository, AppConfigRepository appConfigRepository,
			RestTemplate restTemplate) {
		this.incidentRepository = incidentRepository;
		this.analysisRepository = analysisRepository;
		this.appConfigRepository = appConfigRepository;
		this.restTemplate = restTemplate;
	}

	
	@Override
	public IncidentReportResponse analyzeIncident(String incidentId) {

		log.info("Starting AI analysis for incidentId={}", incidentId);

		IncidentEntity incident = incidentRepository.findById(incidentId)
				.orElseThrow(() -> new RuntimeException("Incident not found"));

		String combinedLogs = readAllLogs(incident.getFolderPath());

		String prompt = buildPrompt(combinedLogs);

		String aiResponse = callOllama(prompt);

		IncidentAnalysisEntity analysisEntity = analysisRepository.findByIncident_IncidentId(incidentId)
				.orElse(new IncidentAnalysisEntity());

		analysisEntity.setIncident(incident);
		analysisEntity.setSummary(extractSummary(aiResponse));
		analysisEntity.setObservationsJson(extractObservations(aiResponse));
		analysisEntity.setRootCausesJson(extractRootCauses(aiResponse));
		analysisEntity.setRecommendationsJson(extractRecommendations(aiResponse));
		analysisEntity.setModelUsed(modelName);
		analysisEntity.setGeneratedAt(LocalDateTime.now());

		analysisRepository.save(analysisEntity);

		incident.setStatus(Constant.ANALYZED);
		incidentRepository.save(incident);

		log.info("AI analysis completed for incidentId={}", incidentId);

		return mapToResponse(incident, analysisEntity);
	}


	@Override
	public IncidentReportResponse getReport(String incidentId) {

		log.info("Fetching AI report for incidentId={}", incidentId);

		IncidentAnalysisEntity analysis = analysisRepository.findByIncident_IncidentId(incidentId)
				.orElseThrow(() -> new RuntimeException("Analysis not found"));

		IncidentEntity incident = analysis.getIncident();

		return mapToResponse(incident, analysis);
	}


	private String readAllLogs(String folderPath) {

		log.info("Reading log files from {}", folderPath);

		StringBuilder logsBuilder = new StringBuilder();

		try {
			Files.list(Paths.get(folderPath)).filter(Files::isRegularFile).forEach(file -> {
				try {
					logsBuilder.append(Files.readString(file)).append("\n");
				} catch (IOException e) {
					log.error("Failed to read log file {}", file, e);
				}
			});
		} catch (IOException e) {
			throw new RuntimeException("Unable to read log files", e);
		}

		return logsBuilder.toString();
	}

	private String buildPrompt(String logs) {

		log.info("Fetching analysis prompt from database");

		AppConfigEntity promptConfig = appConfigRepository.findByConfigNameAndIsActiveTrue("analysis_prompt")
				.orElseThrow(() -> new RuntimeException("Analysis prompt configuration not found or inactive"));

		String promptTemplate = promptConfig.getConfigValue();

		return promptTemplate + logs;
	}

	private String callOllama(String prompt) {

		log.info("Calling Ollama model {}", modelName);

		try {
			Map<String, Object> requestBody = new HashMap<>();
			requestBody.put("model", modelName);
			requestBody.put("prompt", prompt);
			requestBody.put("stream", false);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(ollamaApiUrl + "/api/generate",
					HttpMethod.POST, request, new ParameterizedTypeReference<Map<String, Object>>() {
					});

			if (response.getBody() == null || !response.getBody().containsKey("response")) {
				log.error("Invalid response from Ollama API: {}", response.getBody());
				throw new RuntimeException("Invalid response from Ollama API");
			}

			return String.valueOf(response.getBody().get("response"));
		} catch (Exception e) {
			log.error("Failed to call Ollama API", e);
			throw new RuntimeException("Failed to call Ollama API: " + e.getMessage(), e);
		}
	}

	private String extractSummary(String aiResponse) {
	    try {
	        JsonNode root = objectMapper.readTree(aiResponse);
	        return root.path("summary").asText();
	    } catch (Exception e) {
	        log.error("Failed to extract summary from AI response", e);
	        return "Summary unavailable";
	    }
	}


	private String extractObservations(String aiResponse) {
	    try {
	        JsonNode root = objectMapper.readTree(aiResponse);
	        JsonNode observationsNode = root.path("observations");
	        return observationsNode.isMissingNode()
	                ? "{}"
	                : objectMapper.writeValueAsString(observationsNode);
	    } catch (Exception e) {
	        log.error("Failed to extract observations", e);
	        return "{}";
	    }
	}


	private String extractRootCauses(String aiResponse) {
	    try {
	        JsonNode root = objectMapper.readTree(aiResponse);
	        JsonNode causesNode = root.path("suspected_root_causes");
	        return causesNode.isMissingNode()
	                ? "[]"
	                : objectMapper.writeValueAsString(causesNode);
	    } catch (Exception e) {
	        log.error("Failed to extract root causes", e);
	        return "[]";
	    }
	}


	
	private String extractRecommendations(String aiResponse) {
	    try {
	        JsonNode root = objectMapper.readTree(aiResponse);
	        JsonNode recommendationsNode = root.path("recommendations");
	        return recommendationsNode.isMissingNode()
	                ? "[]"
	                : objectMapper.writeValueAsString(recommendationsNode);
	    } catch (Exception e) {
	        log.error("Failed to extract recommendations", e);
	        return "[]";
	    }
	}


	private IncidentReportResponse mapToResponse(IncidentEntity incident, IncidentAnalysisEntity analysis) {
		IncidentReportResponse response = new IncidentReportResponse();
		response.setIncidentId(incident.getIncidentId());
		response.setStatus(Constant.COMPLETED);
		response.setSummary(analysis.getSummary());

		Map<String, Object> observations = new HashMap<>();
		observations.put("details", analysis.getObservationsJson());

		response.setObservations(observations);
		
		// Parse JSON strings to Lists
		try {
			if (analysis.getRootCausesJson() != null && !analysis.getRootCausesJson().isEmpty()) {
				JsonNode rootCausesNode = objectMapper.readTree(analysis.getRootCausesJson());
				response.setSuspected_root_causes(
						objectMapper.convertValue(rootCausesNode, 
								objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)));
			} else {
				response.setSuspected_root_causes(List.of());
			}
		} catch (Exception e) {
			log.warn("Failed to parse root causes JSON, using empty list", e);
			response.setSuspected_root_causes(List.of());
		}

		try {
			if (analysis.getRecommendationsJson() != null && !analysis.getRecommendationsJson().isEmpty()) {
				JsonNode recommendationsNode = objectMapper.readTree(analysis.getRecommendationsJson());
				response.setRecommendations(
						objectMapper.convertValue(recommendationsNode,
								objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)));
			} else {
				response.setRecommendations(List.of());
			}
		} catch (Exception e) {
			log.warn("Failed to parse recommendations JSON, using empty list", e);
			response.setRecommendations(List.of());
		}

		response.setModel_used(analysis.getModelUsed());
		response.setGenerated_at(analysis.getGeneratedAt());

		return response;
	}

}