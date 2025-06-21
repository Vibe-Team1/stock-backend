package com.example.springgpt.dto;

public class TradeResponseDto {
    public boolean success;
    public String tradeId;

    public TradeResponseDto(boolean success, String tradeId) {
        this.success = success;
        this.tradeId = tradeId;
    }
}
