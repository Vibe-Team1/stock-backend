package com.hts.service;

import com.hts.domain.Stock;
import com.hts.dto.StockSearchResponse;
import com.hts.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockService {

    private final StockRepository stockRepository;

    @Autowired
    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public List<StockSearchResponse> searchStocks(String keyword) {
        List<Stock> stocks = stockRepository.searchStocks(keyword);
        return stocks.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private StockSearchResponse convertToResponse(Stock stock) {
        return new StockSearchResponse(
                stock.getTicker(),
                stock.getName(),
                stock.getCurrentPrice(),
                stock.getPer(),
                stock.getPbr()
        );
    }
} 