package com.example.springgpt.repository.csv;

import com.example.springgpt.domain.Stock;
import com.example.springgpt.repository.StockRepository;
import com.example.springgpt.utils.CsvUtil;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CsvStockRepository implements StockRepository {
    private final List<Stock> stockList = new ArrayList<>();

    @PostConstruct
    public void init() {
        try {
            List<CSVRecord> records = CsvUtil.readCsv("csv/국내주식기본조회v1.csv");
            for (CSVRecord record : records) {
                Stock stock = new Stock(
                    record.get("\uFEFF종목코드"),  // BOM-prefixed
                    record.get("종목명"),
                    Double.parseDouble(record.get("현재가")),
                    Double.parseDouble(record.get("PER")),
                    Double.parseDouble(record.get("PBR"))
                );
                stockList.add(stock);
            }
        } catch (Exception e) {
            System.err.println("[CsvStockRepository] Failed to load stock data: " + e.getMessage());
        }
    }

    @Override
    public List<Stock> search(String keyword) {
        String lower = keyword.toLowerCase();
        return stockList.stream()
                .filter(s -> s.getTicker().toLowerCase().contains(lower)
                        || s.getName().toLowerCase().contains(lower))
                .toList();
    }
}
