package com.hts.repository;

import com.hts.domain.Stock;
import java.util.List;
import java.util.Optional;

public interface StockRepository {
    List<Stock> searchStocks(String keyword);
    Optional<Stock> findByTicker(String ticker);
    List<Stock> findAll();
} 