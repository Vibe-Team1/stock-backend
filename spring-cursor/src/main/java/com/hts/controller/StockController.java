package com.hts.controller;

import com.hts.dto.StockSearchResponse;
import com.hts.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "*")
public class StockController {

    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<StockSearchResponse>> searchStocks(@RequestParam("q") String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        List<StockSearchResponse> stocks = stockService.searchStocks(keyword.trim());
        return ResponseEntity.ok(stocks);
    }
} 