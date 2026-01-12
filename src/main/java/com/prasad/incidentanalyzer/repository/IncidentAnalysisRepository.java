package com.prasad.incidentanalyzer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prasad.incidentanalyzer.entity.IncidentAnalysisEntity;

public interface IncidentAnalysisRepository extends JpaRepository<IncidentAnalysisEntity, Long> {
	Optional<IncidentAnalysisEntity> findByIncident_IncidentId(String incidentId);
}