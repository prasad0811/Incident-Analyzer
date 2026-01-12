package com.prasad.incidentanalyzer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "incident_analysis")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IncidentAnalysisEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "incident_id", nullable = false)
	private IncidentEntity incident;

	@Column(name = "summary", columnDefinition = "TEXT")
	private String summary;

	@Column(name = "observations", columnDefinition = "TEXT")
	private String observationsJson;

	@Column(name = "root_causes", columnDefinition = "TEXT")
	private String rootCausesJson;

	@Column(name = "recommendations", columnDefinition = "TEXT")
	private String recommendationsJson;

	@Column(name = "model_used")
	private String modelUsed;

	@Column(name = "generated_at")
	private LocalDateTime generatedAt;
}