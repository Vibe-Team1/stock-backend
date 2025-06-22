package com.hts.chart;

import java.time.LocalDateTime;
import java.util.List;

public interface ChartRepository {
    List<ChartData> findChartData(String ticker, LocalDateTime from, LocalDateTime to, ChartInterval interval);
    List<ChartData> findByTicker(String ticker);
} 