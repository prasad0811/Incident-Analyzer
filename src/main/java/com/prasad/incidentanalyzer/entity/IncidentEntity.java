package com.prasad.incidentanalyzer.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "incidents")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IncidentEntity {

	@Id
	@Column(name = "incident_id", nullable = false, updatable = false)
	private String incidentId;

	@Column(name = "title")
	private String title;

	@Column(name = "status", nullable = false)
	private String status; // CREATED, UPLOADED, ANALYZED

	@Column(name = "folder_path", nullable = false)
	private String folderPath;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "incident", cascade = CascadeType.ALL)
	private List<IncidentLogFileEntity> logFiles;

	@OneToOne(mappedBy = "incident", cascade = CascadeType.ALL)
	private IncidentAnalysisEntity analysis;

}