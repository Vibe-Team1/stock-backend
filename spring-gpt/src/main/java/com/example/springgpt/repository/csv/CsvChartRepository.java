package com.example.springgpt.repository.csv;

import com.example.springgpt.domain.ChartCandle;
import com.example.springgpt.repository.ChartRepository;
import com.example.springgpt.utils.CsvUtil;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CsvChartRepository implements ChartRepository {
    private final List<ChartCandle> allCandles = new ArrayList<>();

    @PostConstruct
    public void init() {
        try {
            List<CSVRecord> records = CsvUtil.readCsv("csv/국내주식분봉차트v1.csv");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (CSVRecord record : records) {
                String date = record.get("영업일자");
                String time = record.get("체결시간"); // e.g., "132100"
                String datetime = date + " " + time.substring(0, 2) + ":" +
                                  time.substring(2, 4) + ":" + time.substring(4, 6);
                LocalDateTime timestamp = LocalDateTime.parse(datetime, formatter);

                ChartCandle candle = new ChartCandle(
                    record.get("종목코드"),
                    timestamp,
                    Double.parseDouble(record.get("시가")),
                    Double.parseDouble(record.get("고가")),
                    Double.parseDouble(record.get("저가")),
                    Double.parseDouble(record.get("현재가")),
                    Long.parseLong(record.get("체결거래량"))
                );
                allCandles.add(candle);
            }
        } catch (Exception e) {
            System.err.println("[CsvChartRepository] Failed to load chart data: " + e.getMessage());
        }
    }

    @Override
    public List<ChartCandle> findByTickerAndRange(String ticker, LocalDateTime from, LocalDateTime to) {
        return allCandles.stream()
                .filter(c -> c.getTicker().equalsIgnoreCase(ticker)
                        && !c.getTimestamp().isBefore(from)
                        && !c.getTimestamp().isAfter(to))
                .toList();
    }
}
