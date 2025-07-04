package com.hts.stock;

import com.hts.stock.StockListResponse;
import com.hts.stock.StockSearchResponse;
import com.hts.stock.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "*")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public ResponseEntity<StockListResponse> getStockList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        if (page < 0 || size <= 0 || size > 100) {
            return ResponseEntity.badRequest().build();
        }

        StockListResponse response = stockService.getStockList(page, size);
        return ResponseEntity.ok(response);
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