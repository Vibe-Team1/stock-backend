package com.hts.common.config;

import com.hts.chart.ChartDataWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@EnableScheduling
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChartDataWebSocketHandler chartDataWebSocketHandler;

    public WebSocketConfig(ChartDataWebSocketHandler chartDataWebSocketHandler) {
        this.chartDataWebSocketHandler = chartDataWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chartDataWebSocketHandler, "/ws/chart")
                .setAllowedOrigins("*");
    }
} 