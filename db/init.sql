DROP TABLE IF EXISTS app_config CASCADE;
DROP TABLE IF EXISTS incident_analysis CASCADE;
DROP TABLE IF EXISTS incident_log_files CASCADE;
DROP TABLE IF EXISTS incidents CASCADE;


CREATE TABLE incidents (
    incident_id VARCHAR(64) PRIMARY KEY,
    title VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    folder_path TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);


CREATE TABLE incident_log_files (
    id BIGSERIAL PRIMARY KEY,
    incident_id VARCHAR(64) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path TEXT NOT NULL,
    file_size BIGINT,
    CONSTRAINT fk_incident_logs
        FOREIGN KEY (incident_id)
        REFERENCES incidents (incident_id)
        ON DELETE CASCADE
);


CREATE TABLE incident_analysis (
    id BIGSERIAL PRIMARY KEY,
    incident_id VARCHAR(64) NOT NULL UNIQUE,
    summary TEXT,
    observations TEXT,
    root_causes TEXT,
    recommendations TEXT,
    model_used VARCHAR(100),
    generated_at TIMESTAMP,
    CONSTRAINT fk_incident_analysis
        FOREIGN KEY (incident_id)
        REFERENCES incidents (incident_id)
        ON DELETE CASCADE
);


CREATE INDEX idx_incident_status
    ON incidents (status);

CREATE INDEX idx_incident_created_at
    ON incidents (created_at);

CREATE INDEX idx_incident_log_incident_id
    ON incident_log_files (incident_id);


CREATE TABLE app_config (
    id BIGSERIAL PRIMARY KEY,
    config_name VARCHAR(255) NOT NULL UNIQUE,
    config_value TEXT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_app_config_name
    ON app_config (config_name);

CREATE INDEX idx_app_config_active
    ON app_config (is_active);


INSERT INTO app_config (config_name, config_value, is_active, created_at)
VALUES (
    'analysis_prompt',
    'You are a production incident analysis AI.

Analyze the following logs and provide:
1. Summary
2. Observations (error spikes, time window, severity)
3. Suspected root causes
4. Recommendations

You MUST respond in valid JSON only.
Do NOT include explanations or markdown.

Logs:
',
    true,
    CURRENT_TIMESTAMP
);
