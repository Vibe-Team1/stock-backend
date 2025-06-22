package com.hts.chart;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository("httpChartRepository")
public class HttpChartRepository implements ChartRepository {

    @Override
    public List<ChartData> findChartData(String ticker, LocalDateTime from, LocalDateTime to, ChartInterval interval) {
        // TODO: Implement HTTP API call to external chart data service
        // This is a placeholder implementation for future integration
        return new ArrayList<>();
    }

    @Override
    public List<ChartData> findByTicker(String ticker) {
        // TODO: Implement HTTP API call to external chart data service
        // This is a placeholder implementation for future integration
        return new ArrayList<>();
    }
} 