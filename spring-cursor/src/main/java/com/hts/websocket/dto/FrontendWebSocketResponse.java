package com.hts.websocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FrontendWebSocketResponse {
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("requestId")
    private String requestId;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("data")
    private Object data;
    
    @JsonProperty("error")
    private String error;
    
    @JsonProperty("timestamp")
    private Long timestamp;
    
    public FrontendWebSocketResponse() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public FrontendWebSocketResponse(String type, String requestId, String status, Object data) {
        this.type = type;
        this.requestId = requestId;
        this.status = status;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    public FrontendWebSocketResponse(String type, String requestId, String status, String error) {
        this.type = type;
        this.requestId = requestId;
        this.status = status;
        this.error = error;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Static factory methods for common responses
    public static FrontendWebSocketResponse success(String type, String requestId, Object data) {
        return new FrontendWebSocketResponse(type, requestId, "SUCCESS", data);
    }
    
    public static FrontendWebSocketResponse error(String type, String requestId, String error) {
        return new FrontendWebSocketResponse(type, requestId, "ERROR", error);
    }
    
    public static FrontendWebSocketResponse pong(String requestId) {
        return new FrontendWebSocketResponse("PONG", requestId, "SUCCESS", null);
    }
    
    // Response types
    public static class ResponseTypes {
        public static final String STOCK_DATA = "STOCK_DATA";
        public static final String CHART_DATA = "CHART_DATA";
        public static final String SUBSCRIPTION_CONFIRMED = "SUBSCRIPTION_CONFIRMED";
        public static final String UNSUBSCRIPTION_CONFIRMED = "UNSUBSCRIPTION_CONFIRMED";
        public static final String PONG = "PONG";
        public static final String ERROR = "ERROR";
    }
    
    // Getters and Setters
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
} 