package com.hts.repository;

import com.hts.domain.ChartData;
import com.hts.domain.ChartInterval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DbChartRepository extends ChartRepository, JpaRepository<ChartData, Long> {

    @Query("SELECT c FROM ChartData c WHERE c.ticker = :ticker AND c.timestamp BETWEEN :from AND :to ORDER BY c.timestamp")
    List<ChartData> findChartDataByTickerAndTimeRange(
            @Param("ticker") String ticker,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Override
    default List<ChartData> findChartData(String ticker, LocalDateTime from, LocalDateTime to, ChartInterval interval) {
        // TODO: Implement interval-based aggregation logic
        return findChartDataByTickerAndTimeRange(ticker, from, to);
    }

    @Override
    default List<ChartData> findByTicker(String ticker) {
        return findByTickerOrderByTimestampAsc(ticker);
    }

    List<ChartData> findByTickerOrderByTimestampAsc(String ticker);
} 