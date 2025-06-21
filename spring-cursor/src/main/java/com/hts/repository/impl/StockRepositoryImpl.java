package com.hts.repository.impl;

import com.hts.domain.Stock;
import com.hts.repository.StockRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class StockRepositoryImpl implements StockRepository {

    private static final String STOCK_DATA_FILE = "국내주식기본조회v1.csv";
    private List<Stock> stockCache;

    public StockRepositoryImpl() {
        loadStockData();
    }

    private void loadStockData() {
        stockCache = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(STOCK_DATA_FILE))) {
            List<String[]> rows = reader.readAll();
            
            // Skip header row
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length >= 13) {
                    Stock stock = new Stock(
                            row[0], // 종목코드 (ticker)
                            row[1], // 종목명 (name)
                            parseBigDecimal(row[2]), // 현재가 (currentPrice)
                            parseBigDecimal(row[8]), // PER
                            parseBigDecimal(row[9])  // PBR
                    );
                    stockCache.add(stock);
                }
            }
        } catch (IOException | CsvException e) {
            System.err.println("Error loading stock data: " + e.getMessage());
        }
    }

    @Override
    public List<Stock> searchStocks(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return stockCache.stream()
                .filter(stock -> 
                    stock.getName().toLowerCase().contains(lowerKeyword) ||
                    stock.getTicker().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Stock> findByTicker(String ticker) {
        return stockCache.stream()
                .filter(stock -> stock.getTicker().equals(ticker))
                .findFirst();
    }

    @Override
    public List<Stock> findAll() {
        return new ArrayList<>(stockCache);
    }

    private BigDecimal parseBigDecimal(String value) {
        try {
            if (value == null || value.trim().isEmpty() || "N/A".equals(value)) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal(value.replace(",", ""));
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
} 