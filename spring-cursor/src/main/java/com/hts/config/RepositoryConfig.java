package com.hts.config;

import com.hts.repository.ChartRepository;
import com.hts.repository.csv.CsvChartRepository;
import com.hts.repository.DbChartRepository;
import com.hts.repository.HttpChartRepository;
import com.hts.repository.WebSocketChartRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RepositoryConfig {

    @Value("${app.chart.repository.type:csv}")
    private String chartRepositoryType;

    @Bean
    @Primary
    public ChartRepository chartRepository(
            CsvChartRepository csvChartRepository,
            DbChartRepository dbChartRepository,
            HttpChartRepository httpChartRepository,
            WebSocketChartRepository webSocketChartRepository) {
        
        return switch (chartRepositoryType.toLowerCase()) {
            case "csv" -> csvChartRepository;
            case "db" -> dbChartRepository;
            case "http" -> httpChartRepository;
            case "websocket" -> webSocketChartRepository;
            default -> csvChartRepository;
        };
    }
} 