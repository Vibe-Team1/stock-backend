package com.hts.websocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FrontendWebSocketRequest {
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("requestId")
    private String requestId;
    
    @JsonProperty("payload")
    private Object payload;
    
    public FrontendWebSocketRequest() {}
    
    public FrontendWebSocketRequest(String type, String requestId, Object payload) {
        this.type = type;
        this.requestId = requestId;
        this.payload = payload;
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
    
    public Object getPayload() {
        return payload;
    }
    
    public void setPayload(Object payload) {
        this.payload = payload;
    }
    
    // Request types
    public static class RequestTypes {
        public static final String GET_STOCK_DATA = "GET_STOCK_DATA";
        public static final String SUBSCRIBE_TO_TICKER = "SUBSCRIBE_TO_TICKER";
        public static final String UNSUBSCRIBE_FROM_TICKER = "UNSUBSCRIBE_FROM_TICKER";
        public static final String GET_CHART_DATA = "GET_CHART_DATA";
        public static final String PING = "PING";
    }
    
    // Payload classes for different request types
    public static class StockDataRequest {
        @JsonProperty("ticker")
        private String ticker;
        
        public StockDataRequest() {}
        
        public StockDataRequest(String ticker) {
            this.ticker = ticker;
        }
        
        public String getTicker() {
            return ticker;
        }
        
        public void setTicker(String ticker) {
            this.ticker = ticker;
        }
    }
    
    public static class ChartDataRequest {
        @JsonProperty("ticker")
        private String ticker;
        
        @JsonProperty("interval")
        private String interval;
        
        @JsonProperty("limit")
        private Integer limit;
        
        public ChartDataRequest() {}
        
        public ChartDataRequest(String ticker, String interval, Integer limit) {
            this.ticker = ticker;
            this.interval = interval;
            this.limit = limit;
        }
        
        public String getTicker() {
            return ticker;
        }
        
        public void setTicker(String ticker) {
            this.ticker = ticker;
        }
        
        public String getInterval() {
            return interval;
        }
        
        public void setInterval(String interval) {
            this.interval = interval;
        }
        
        public Integer getLimit() {
            return limit;
        }
        
        public void setLimit(Integer limit) {
            this.limit = limit;
        }
    }
} 