package com.example.springgpt.service;

import com.example.springgpt.domain.Stock;
import com.example.springgpt.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {
    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public List<Stock> search(String keyword) {
        return stockRepository.search(keyword);
    }
}
