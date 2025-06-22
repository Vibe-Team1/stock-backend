package com.hts.chart;

import com.hts.websocket.KoreaInvestmentWebSocketClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository("webSocketChartRepository")
public class WebSocketChartRepository implements ChartRepository {

    private final KoreaInvestmentWebSocketClient webSocketClient;

    public WebSocketChartRepository(KoreaInvestmentWebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

    @Override
    public List<ChartData> findChartData(String ticker, LocalDateTime from, LocalDateTime to, ChartInterval interval) {
        // For real-time data, we connect to WebSocket and return empty list
        // The actual data will be processed by the WebSocket client
        if (!webSocketClient.isConnected()) {
            webSocketClient.connect(ticker);
        }

        // Return empty list as real-time data is handled by WebSocket client
        return new ArrayList<>();
    }

    @Override
    public List<ChartData> findByTicker(String ticker) {
        // For real-time data, we connect to WebSocket and return empty list
        // The actual data will be processed by the WebSocket client
        if (!webSocketClient.isConnected()) {
            webSocketClient.connect(ticker);
        }

        // Return empty list as real-time data is handled by WebSocket client
        return new ArrayList<>();
    }

    public void disconnect() {
        webSocketClient.disconnect();
    }

    public boolean isConnected() {
        return webSocketClient.isConnected();
    }
} 