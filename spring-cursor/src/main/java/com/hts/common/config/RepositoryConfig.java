package com.hts.common.config;

import com.hts.chart.ChartRepository;
import com.hts.stock.StockRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RepositoryConfig {

    @Value("${app.chart.repository.type:csv}")
    private String chartRepositoryType;

    @Value("${app.stock.repository.type:csv}")
    private String stockRepositoryType;

    @Bean
    @Primary
    public ChartRepository chartRepository(
            @Qualifier("csvChartRepository") ChartRepository csvChartRepository,
            @Qualifier("dbChartRepository") ChartRepository dbChartRepository,
            @Qualifier("httpChartRepository") ChartRepository httpChartRepository,
            @Qualifier("webSocketChartRepository") ChartRepository webSocketChartRepository) {

        return switch (chartRepositoryType.toLowerCase()) {
            case "db" -> dbChartRepository;
            case "http" -> httpChartRepository;
            case "websocket" -> webSocketChartRepository;
            default -> csvChartRepository; // Default to csv
        };
    }

    @Bean
    @Primary
    public StockRepository stockRepository(
            @Qualifier("csvStockRepository") StockRepository csvStockRepository,
            @Qualifier("dbStockRepository") StockRepository dbStockRepository) {

        return switch (stockRepositoryType.toLowerCase()) {
            case "db" -> dbStockRepository;
            default -> csvStockRepository; // Default to csv
        };
    }
} 