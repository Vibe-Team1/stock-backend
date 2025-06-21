package com.example.springgpt.dto;

import java.time.LocalDateTime;

public class ChartCandleDto {
    public String ticker;
    public LocalDateTime timestamp;
    public double open, high, low, close;
    public long volume;

    public ChartCandleDto(String ticker, LocalDateTime timestamp, double open, double high,
                          double low, double close, long volume) {
        this.ticker = ticker;
        this.timestamp = timestamp;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }
}
