package com.hts.stock;

import java.math.BigDecimal;

public class StockSearchResponse {
    private String ticker;
    private String name;
    private BigDecimal currentPrice;
    private BigDecimal per;
    private BigDecimal pbr;

    public StockSearchResponse() {}

    public StockSearchResponse(String ticker, String name, BigDecimal currentPrice, BigDecimal per, BigDecimal pbr) {
        this.ticker = ticker;
        this.name = name;
        this.currentPrice = currentPrice;
        this.per = per;
        this.pbr = pbr;
    }

    // Getters and Setters
    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getPer() {
        return per;
    }

    public void setPer(BigDecimal per) {
        this.per = per;
    }

    public BigDecimal getPbr() {
        return pbr;
    }

    public void setPbr(BigDecimal pbr) {
        this.pbr = pbr;
    }
} 