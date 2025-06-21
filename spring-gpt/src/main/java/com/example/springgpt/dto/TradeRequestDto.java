package com.example.springgpt.dto;

import java.time.LocalDateTime;

public class TradeRequestDto {
    public String accountId;
    public String ticker;
    public double price;
    public int quantity;
    public LocalDateTime timestamp;
}
