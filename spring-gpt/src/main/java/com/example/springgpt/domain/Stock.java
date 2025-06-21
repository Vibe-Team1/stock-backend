package com.example.springgpt.domain;

public class Stock {
    private String ticker;
    private String name;
    private double currentPrice;
    private double per;
    private double pbr;

    public Stock(String ticker, String name, double currentPrice, double per, double pbr) {
        this.ticker = ticker;
        this.name = name;
        this.currentPrice = currentPrice;
        this.per = per;
        this.pbr = pbr;
    }

    public String getTicker() {
        return ticker;
    }

    public String getName() {
        return name;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public double getPer() {
        return per;
    }

    public double getPbr() {
        return pbr;
    }
}
