package com.prasad.incidentanalyzer.serviceImpl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.prasad.incidentanalyzer.constant.Constant;
import com.prasad.incidentanalyzer.dto.IncidentSummaryResponse;
import com.prasad.incidentanalyzer.dto.IncidentUploadResponse;
import com.prasad.incidentanalyzer.entity.IncidentEntity;
import com.prasad.incidentanalyzer.entity.IncidentLogFileEntity;
import com.prasad.incidentanalyzer.repository.IncidentLogFileRepository;
import com.prasad.incidentanalyzer.repository.IncidentRepository;
import com.prasad.incidentanalyzer.service.IncidentService;

@Service
@Transactional
public class IncidentServiceImpl implements IncidentService {

	private static final Logger log = LoggerFactory.getLogger(IncidentServiceImpl.class);

	private final IncidentRepository incidentRepository;
	private final IncidentLogFileRepository logFileRepository;

	@Value("${incident.storage.base-path}")
	private String baseStoragePath;

	public IncidentServiceImpl(IncidentRepository incidentRepository, IncidentLogFileRepository logFileRepository) {
		this.incidentRepository = incidentRepository;
		this.logFileRepository = logFileRepository;
	}

	@Override
	public IncidentUploadResponse createIncident(List<MultipartFile> logs, String title) {

		log.info("Creating new incident. Log file count={}", logs.size());

		String incidentId = UUID.randomUUID().toString();
		String incidentFolderPath = baseStoragePath + File.separator + incidentId;

		try {
			Files.createDirectories(Paths.get(incidentFolderPath));
			log.info("Incident folder created at {}", incidentFolderPath);
		} catch (Exception ex) {
			log.error("Failed to create incident folder", ex);
			throw new RuntimeException("Unable to create incident directory");
		}

		IncidentEntity incident = new IncidentEntity();
		incident.setIncidentId(incidentId);
		incident.setTitle(title);
		incident.setStatus(Constant.UPLOADED);
		incident.setFolderPath(incidentFolderPath);
		incident.setCreatedAt(LocalDateTime.now());

		incidentRepository.save(incident);
		log.info("Incident metadata saved in DB for incidentId={}", incidentId);

		List<IncidentLogFileEntity> logEntities = new ArrayList<>();

		for (MultipartFile file : logs) {
			try {
				Path targetPath = Paths.get(incidentFolderPath, file.getOriginalFilename());

				Files.copy(file.getInputStream(), targetPath);

				IncidentLogFileEntity logFile = new IncidentLogFileEntity();
				logFile.setFileName(file.getOriginalFilename());
				logFile.setFilePath(targetPath.toString());
				logFile.setFileSize(file.getSize());
				logFile.setIncident(incident);

				logEntities.add(logFile);

				log.info("Stored log file {}", file.getOriginalFilename());

			} catch (Exception ex) {
				log.error("Failed to store log file {}", file.getOriginalFilename(), ex);
				throw new RuntimeException("Log file upload failed");
			}
		}

		logFileRepository.saveAll(logEntities);

		return new IncidentUploadResponse(incidentId, "UPLOADED", "Log files uploaded successfully");
	}

	
	@Override
	public List<IncidentSummaryResponse> getAllIncidents() {

		log.info("Fetching all incidents");

		List<IncidentEntity> incidents = incidentRepository.findAll();
		List<IncidentSummaryResponse> responseList = new ArrayList<>();

		for (IncidentEntity entity : incidents) {
			IncidentSummaryResponse response = new IncidentSummaryResponse();
			response.setIncidentId(entity.getIncidentId());
			response.setTitle(entity.getTitle());
			response.setStatus(entity.getStatus());
			response.setCreatedAt(entity.getCreatedAt());

			responseList.add(response);
		}

		log.info("Total incidents found={}", responseList.size());
		return responseList;
	}

	

	@Override
	public IncidentSummaryResponse getIncident(String incidentId) {

		log.info("Fetching incident details for incidentId={}", incidentId);

		IncidentEntity incident = incidentRepository.findById(incidentId)
				.orElseThrow(() -> new RuntimeException("Incident not found"));

		IncidentSummaryResponse response = new IncidentSummaryResponse();
		response.setIncidentId(incident.getIncidentId());
		response.setTitle(incident.getTitle());
		response.setStatus(incident.getStatus());
		response.setCreatedAt(incident.getCreatedAt());

		return response;
	}

	
}
