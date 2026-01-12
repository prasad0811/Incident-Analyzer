package com.prasad.incidentanalyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prasad.incidentanalyzer.entity.IncidentEntity;

public interface IncidentRepository extends JpaRepository<IncidentEntity, String> {

}
