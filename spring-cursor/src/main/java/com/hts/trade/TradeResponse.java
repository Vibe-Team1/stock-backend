package com.hts.trade;

public class TradeResponse {
    private boolean success;
    private String tradeId;
    private String message;

    public TradeResponse() {}

    public TradeResponse(boolean success, String tradeId, String message) {
        this.success = success;
        this.tradeId = tradeId;
        this.message = message;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
} 