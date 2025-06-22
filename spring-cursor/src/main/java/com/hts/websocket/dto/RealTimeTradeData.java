package com.hts.websocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class RealTimeTradeData {

    @JsonProperty("ticker")
    private String ticker;

    @JsonProperty("tradeTime")
    private String tradeTime;

    @JsonProperty("currentPrice")
    private BigDecimal currentPrice;

    @JsonProperty("open")
    private BigDecimal open;

    @JsonProperty("high")
    private BigDecimal high;

    @JsonProperty("low")
    private BigDecimal low;

    @JsonProperty("volume")
    private Long volume;

    @JsonProperty("totalValue")
    private Long totalValue;

    @JsonProperty("bizDate")
    private String bizDate;

    public RealTimeTradeData() {}

    public RealTimeTradeData(String ticker, String tradeTime, BigDecimal currentPrice,
                           BigDecimal open, BigDecimal high, BigDecimal low,
                           Long volume, Long totalValue, String bizDate) {
        this.ticker = ticker;
        this.tradeTime = tradeTime;
        this.currentPrice = currentPrice;
        this.open = open;
        this.high = high;
        this.low = low;
        this.volume = volume;
        this.totalValue = totalValue;
        this.bizDate = bizDate;
    }

    // Getters and Setters
    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    public Long getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(Long totalValue) {
        this.totalValue = totalValue;
    }

    public String getBizDate() {
        return bizDate;
    }

    public void setBizDate(String bizDate) {
        this.bizDate = bizDate;
    }

    @Override
    public String toString() {
        return "RealTimeTradeData{" +
                "ticker='" + ticker + '\'' +
                ", tradeTime='" + tradeTime + '\'' +
                ", currentPrice=" + currentPrice +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", volume=" + volume +
                ", totalValue=" + totalValue +
                ", bizDate='" + bizDate + '\'' +
                '}';
    }
}
