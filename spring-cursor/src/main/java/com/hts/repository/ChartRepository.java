package com.hts.repository;

import com.hts.domain.ChartData;
import com.hts.domain.ChartInterval;
import java.time.LocalDateTime;
import java.util.List;

public interface ChartRepository {
    List<ChartData> findChartData(String ticker, LocalDateTime from, LocalDateTime to, ChartInterval interval);
    List<ChartData> findByTicker(String ticker);
} 