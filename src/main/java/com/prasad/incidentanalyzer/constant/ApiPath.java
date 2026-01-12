package com.prasad.incidentanalyzer.constant;

/**
 * Centralized class for all API endpoints in the Incident Analyzer project.
 
 */
public final class ApiPath {

    private ApiPath() {
        
    }
   
    public static final String INCIDENT_BASE = "/incident";
    public static final String INCIDENT_UPLOAD = "/upload";
    public static final String INCIDENT_GET_ALL ="/all";
    public static final String INCIDENT_GET_BY_ID = "/{id}";
    public static final String INCIDENT_DELETE = "/delete/{id}";

   
    public static final String AI_BASE = "/ai";
    public static final String AI_ANALYZE = "/analyze/{incidentId}";
    public static final String AI_REPORT = "/report/{incidentId}";

   
    public static final String HEALTH_CHECK = "/health";
    public static final String VERSION = "/version";
}
