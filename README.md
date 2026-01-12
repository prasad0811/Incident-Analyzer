# AI-Powered Incident Analyzer

## Overview

The **AI-Powered Incident Analyzer** is a Java-based application that automates the analysis of production log files using a local Large Language Model (LLM).  
It enables developers and operations teams to quickly identify **errors, root causes, and actionable recommendations** from logs without sending sensitive data to external services.  

The system is designed for **local execution**, ensuring **data privacy**, **rapid analysis**, and **scalable deployment** using Docker.

---

## Key Features

- Accepts **log files** from production or staging environments.
- Generates a **unique incident ID** for each log upload.
- Stores logs locally and references them in a **PostgreSQL database**.
- Performs **AI-powered analysis** using the Ollama LLM.
- Generates a **structured report** including:
  - Summary of incidents
  - Observations and error patterns
  - Suspected root causes
  - Actionable recommendations
- Provides REST APIs for uploading logs, retrieving incidents, and fetching AI analysis reports.

---

## Technology Stack

| Technology | Purpose |
|------------|---------|
| **Java 17** | Core backend development |
| **Spring Boot** | REST API, service layer, dependency injection |
| **PostgreSQL** | Database for storing incident metadata and analysis results |
| **Docker** | Containerized deployment for app, DB, and LLM |
| **Ollama 3.2-3B** | Local Large Language Model for AI-driven log analysis |
| **SLF4J / Logback** | Application logging |

---

## How It Works

1. **User Uploads Log File**  
   - Endpoint: `POST /incident/upload`
   - System generates a unique incident ID and stores the log in a local folder.

2. **AI Analysis**  
   - Endpoint: `POST /ai/analyze/{incidentId}`
   - The local Ollama model processes logs to extract:
     - Summary
     - Observations
     - Root causes
     - Recommendations
   - Analysis results are saved in the database.

3. **Fetch Incident & Report**  
   - `GET /incident/{incidentId}` → Retrieve incident metadata.
   - `GET /ai/report/{incidentId}` → Retrieve AI analysis report.

4. **Data Privacy**  
   - Logs are stored locally.
   - AI processing uses a **local LLM** (Ollama), **no data leaves your environment**.

---

## Using Local LLM Model (Ollama)

- Ollama 3.2-3B runs in a **Docker container** alongside the application.
- Provides **fast inference** for log analysis without internet dependency.
- Ensures **sensitive log data never leaves local infrastructure**.
- Supports **scalable AI analysis**, allowing multiple incidents to be processed independently.

---

## Benefits

- **Accelerates incident triaging** by automating log analysis.
- **Reduces human error** in identifying root causes.
- **Enhances security** by keeping logs and AI processing local.
- **Containerized deployment** makes it easy to run anywhere with Docker.
- Demonstrates **practical use of AI in real production environments** for observability and DevOps.

---

## Getting Started

1. Clone the repository.
2. Ensure **Docker** is installed and running.
3. Build and start the system:
   ```bash
   docker-compose up -d
