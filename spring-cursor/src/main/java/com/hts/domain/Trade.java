package com.hts.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Trade {
    private String tradeId;
    private String accountId;
    private String ticker;
    private BigDecimal price;
    private Long quantity;
    private LocalDateTime timestamp;
    private TradeType type;

    public Trade() {}

    public Trade(String tradeId, String accountId, String ticker, BigDecimal price, 
                Long quantity, LocalDateTime timestamp, TradeType type) {
        this.tradeId = tradeId;
        this.accountId = accountId;
        this.ticker = ticker;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = timestamp;
        this.type = type;
    }

    // Getters and Setters
    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public TradeType getType() {
        return type;
    }

    public void setType(TradeType type) {
        this.type = type;
    }

    public enum TradeType {
        BUY, SELL
    }
} 