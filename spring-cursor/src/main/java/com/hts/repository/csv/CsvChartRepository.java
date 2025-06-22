package com.hts.repository.csv;

import com.hts.domain.ChartData;
import com.hts.domain.ChartInterval;
import com.hts.repository.ChartRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CsvChartRepository implements ChartRepository {

    private static final String CHART_DATA_FILE = "국내주식분봉차트v1.csv";
    private List<ChartData> chartDataCache;

    public CsvChartRepository() {
        loadChartData();
    }

    private void loadChartData() {
        chartDataCache = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(CHART_DATA_FILE))) {
            List<String[]> rows = reader.readAll();
            
            // Skip header row
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length >= 10) {
                    ChartData chartData = new ChartData(
                            row[1], // 종목코드 (ticker)
                            parseDateTime(row[2], row[3]), // 영업일자 + 체결시간
                            parseBigDecimal(row[5]), // 시가 (open)
                            parseBigDecimal(row[6]), // 고가 (high)
                            parseBigDecimal(row[7]), // 저가 (low)
                            parseBigDecimal(row[4]), // 현재가 (close)
                            parseLong(row[8]) // 체결거래량 (volume)
                    );
                    chartDataCache.add(chartData);
                }
            }
        } catch (IOException | CsvException e) {
            System.err.println("Error loading chart data: " + e.getMessage());
        }
    }

    @Override
    public List<ChartData> findChartData(String ticker, LocalDateTime from, LocalDateTime to, ChartInterval interval) {
        return chartDataCache.stream()
                .filter(data -> data.getTicker().equals(ticker))
                .filter(data -> !data.getTimestamp().isBefore(from))
                .filter(data -> !data.getTimestamp().isAfter(to))
                .collect(Collectors.toList());
    }

    @Override
    public List<ChartData> findByTicker(String ticker) {
        return chartDataCache.stream()
                .filter(data -> data.getTicker().equals(ticker))
                .collect(Collectors.toList());
    }

    private LocalDateTime parseDateTime(String dateStr, String timeStr) {
        try {
            // Parse date: YYYYMMDD format
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
            
            // Parse time: HHMMSS format
            int hour = Integer.parseInt(timeStr.substring(0, 2));
            int minute = Integer.parseInt(timeStr.substring(2, 4));
            int second = Integer.parseInt(timeStr.substring(4, 6));
            LocalTime time = LocalTime.of(hour, minute, second);
            
            return LocalDateTime.of(date, time);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }

    private BigDecimal parseBigDecimal(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal(value.replace(",", ""));
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private Long parseLong(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return 0L;
            }
            return Long.parseLong(value.replace(",", ""));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
} 