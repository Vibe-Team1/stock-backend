package com.hts.controller;

import com.hts.dto.TradeRequest;
import com.hts.dto.TradeResponse;
import com.hts.service.TradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/trade")
@CrossOrigin(origins = "*")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping("/buy")
    public ResponseEntity<TradeResponse> buyStock(@Valid @RequestBody TradeRequest request) {
        TradeResponse response = tradeService.buyStock(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/sell")
    public ResponseEntity<TradeResponse> sellStock(@Valid @RequestBody TradeRequest request) {
        TradeResponse response = tradeService.sellStock(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
} 