package com.example.springgpt.repository;

import com.example.springgpt.domain.Stock;

import java.util.List;

public interface StockRepository {
    List<Stock> search(String keyword);
}
