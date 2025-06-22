package com.hts.trade;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TradeRequest {
    @NotBlank(message = "Account ID is required")
    private String accountId;

    @NotBlank(message = "Ticker is required")
    private String ticker;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Long quantity;

    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;

    public TradeRequest() {}

    public TradeRequest(String accountId, String ticker, BigDecimal price, Long quantity, LocalDateTime timestamp) {
        this.accountId = accountId;
        this.ticker = ticker;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    // Getters and Setters
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
} 