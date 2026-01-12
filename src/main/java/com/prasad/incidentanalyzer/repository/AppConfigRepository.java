package com.prasad.incidentanalyzer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prasad.incidentanalyzer.entity.AppConfigEntity;

public interface AppConfigRepository extends JpaRepository<AppConfigEntity, Long> {
	Optional<AppConfigEntity> findByConfigNameAndIsActiveTrue(String configName);
}
