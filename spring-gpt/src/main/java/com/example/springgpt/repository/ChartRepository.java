package com.example.springgpt.repository;

import com.example.springgpt.domain.ChartCandle;

import java.time.LocalDateTime;
import java.util.List;

public interface ChartRepository {
    List<ChartCandle> findByTickerAndRange(String ticker, LocalDateTime from, LocalDateTime to);
}
