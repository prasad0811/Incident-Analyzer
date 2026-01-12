package com.prasad.incidentanalyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prasad.incidentanalyzer.entity.IncidentLogFileEntity;

public interface IncidentLogFileRepository extends JpaRepository<IncidentLogFileEntity, Long> {

}
