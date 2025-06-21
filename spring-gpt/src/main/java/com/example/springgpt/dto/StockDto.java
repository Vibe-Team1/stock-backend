package com.example.springgpt.dto;

public class StockDto {
    public String ticker;
    public String name;
    public double currentPrice;
    public double per;
    public double pbr;

    public StockDto(String ticker, String name, double currentPrice, double per, double pbr) {
        this.ticker = ticker;
        this.name = name;
        this.currentPrice = currentPrice;
        this.per = per;
        this.pbr = pbr;
    }
}
