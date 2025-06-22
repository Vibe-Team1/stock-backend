package com.hts.chart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ChartDataScheduler {

    private final ChartService chartService;
    private final ChartDataWebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;

    public ChartDataScheduler(ChartService chartService, ChartDataWebSocketHandler webSocketHandler, ObjectMapper objectMapper) {
        this.chartService = chartService;
        this.webSocketHandler = webSocketHandler;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedRate = 5000) // 5 seconds
    public void sendChartData() {
        // Example: Fetch data for a default ticker
        String ticker = "005930"; // Samsung Electronics
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = to.minusMinutes(10);
        ChartInterval interval = ChartInterval.ONE_MINUTE;

        List<ChartDataResponse> chartData = chartService.getChartData(ticker, from, to, interval);

        try {
            String chartDataJson = objectMapper.writeValueAsString(chartData);
            webSocketHandler.broadcast(chartDataJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
} 