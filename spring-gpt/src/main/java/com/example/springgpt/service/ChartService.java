package com.example.springgpt.service;

import com.example.springgpt.domain.ChartCandle;
import com.example.springgpt.repository.ChartRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChartService {
    private final ChartRepository chartRepository;

    public ChartService(ChartRepository chartRepository) {
        this.chartRepository = chartRepository;
    }

    public List<ChartCandle> findByTickerAndRange(String ticker, LocalDateTime from, LocalDateTime to) {
        return chartRepository.findByTickerAndRange(ticker, from, to);
    }
}
