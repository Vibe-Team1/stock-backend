package com.example.springgpt.controller;

import com.example.springgpt.dto.TradeRequestDto;
import com.example.springgpt.dto.TradeResponseDto;
import com.example.springgpt.service.TradeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trade")
public class TradeController {
    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping("/buy")
    public TradeResponseDto buy(@RequestBody TradeRequestDto request) {
        return tradeService.buy(request);
    }

    @PostMapping("/sell")
    public TradeResponseDto sell(@RequestBody TradeRequestDto request) {
        return tradeService.sell(request);
    }
}
