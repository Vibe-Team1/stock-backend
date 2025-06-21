package com.example.springgpt.domain;

import java.time.LocalDateTime;

public class ChartCandle {
    private String ticker;
    private LocalDateTime timestamp;
    private double open, high, low, close;
    private long volume;

    public ChartCandle(String ticker, LocalDateTime timestamp, double open, double high,
                       double low, double close, long volume) {
        this.ticker = ticker;
        this.timestamp = timestamp;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public String getTicker() {
        return ticker;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public double getOpen() {
        return open;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getClose() {
        return close;
    }

    public long getVolume() {
        return volume;
    }
}
