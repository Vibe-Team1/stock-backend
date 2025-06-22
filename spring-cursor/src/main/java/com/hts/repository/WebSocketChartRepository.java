package com.hts.repository;

import com.hts.domain.ChartData;
import com.hts.domain.ChartInterval;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class WebSocketChartRepository implements ChartRepository {

    @Override
    public List<ChartData> findChartData(String ticker, LocalDateTime from, LocalDateTime to, ChartInterval interval) {
        // TODO: Implement WebSocket connection to real-time chart data service
        // This is a placeholder implementation for future integration
        return new ArrayList<>();
    }

    @Override
    public List<ChartData> findByTicker(String ticker) {
        // TODO: Implement WebSocket connection to real-time chart data service
        // This is a placeholder implementation for future integration
        return new ArrayList<>();
    }
} 