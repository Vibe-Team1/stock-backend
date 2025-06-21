package com.example.springgpt.service;

import com.example.springgpt.dto.TradeRequestDto;
import com.example.springgpt.dto.TradeResponseDto;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TradeService {

    public TradeResponseDto buy(TradeRequestDto request) {
        return new TradeResponseDto(true, UUID.randomUUID().toString());
    }

    public TradeResponseDto sell(TradeRequestDto request) {
        return new TradeResponseDto(true, UUID.randomUUID().toString());
    }
}
