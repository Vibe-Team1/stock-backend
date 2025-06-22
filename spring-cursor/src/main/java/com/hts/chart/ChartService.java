package com.hts.chart;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChartService {

    private final ChartRepository chartRepository;

    public ChartService(ChartRepository chartRepository) {
        this.chartRepository = chartRepository;
    }

    public List<ChartDataResponse> getChartData(String ticker, LocalDateTime from, LocalDateTime to, ChartInterval interval) {
        List<ChartData> chartData = chartRepository.findChartData(ticker, from, to, interval);
        return chartData.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private ChartDataResponse convertToResponse(ChartData chartData) {
        return new ChartDataResponse(
                chartData.getTicker(),
                chartData.getTimestamp(),
                chartData.getOpen(),
                chartData.getHigh(),
                chartData.getLow(),
                chartData.getClose(),
                chartData.getVolume()
        );
    }
} 