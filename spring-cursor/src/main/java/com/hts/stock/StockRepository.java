package com.hts.stock;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface StockRepository {
    Optional<Stock> findByTicker(String ticker);
    List<Stock> searchStocks(String keyword);
    Page<Stock> findAll(Pageable pageable);
} 