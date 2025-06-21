package com.example.springgpt.controller;

import com.example.springgpt.dto.StockDto;
import com.example.springgpt.service.StockService;
import com.example.springgpt.domain.Stock;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/search")
    public List<StockDto> search(@RequestParam("q") String query) {
        return stockService.search(query).stream()
                .map(s -> new StockDto(s.getTicker(), s.getName(), s.getCurrentPrice(), s.getPer(), s.getPbr()))
                .toList();
    }
}
