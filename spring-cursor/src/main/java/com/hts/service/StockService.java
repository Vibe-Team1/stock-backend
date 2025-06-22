package com.hts.service;

import com.hts.domain.Stock;
import com.hts.dto.StockListResponse;
import com.hts.dto.StockSearchResponse;
import com.hts.exception.StockNotFoundException;
import com.hts.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public List<StockSearchResponse> searchStocks(String keyword) {
        List<Stock> stocks = stockRepository.searchStocks(keyword);
        return stocks.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public StockListResponse getStockList(int page, int size) {
        List<Stock> allStocks = stockRepository.findAll();
        
        // Calculate pagination
        int totalElements = allStocks.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, totalElements);
        
        // Get paginated stocks
        List<Stock> paginatedStocks = allStocks.subList(startIndex, endIndex);
        
        // Convert to DTOs
        List<StockListResponse.StockListItem> stockItems = paginatedStocks.stream()
                .map(this::convertToStockListItem)
                .collect(Collectors.toList());
        
        StockListResponse.PaginationInfo paginationInfo = new StockListResponse.PaginationInfo(
                page, size, totalElements, totalPages
        );
        
        return new StockListResponse(stockItems, paginationInfo);
    }

    public Stock getStockByTicker(String ticker) {
        return stockRepository.findByTicker(ticker)
                .orElseThrow(() -> new StockNotFoundException("Stock not found with ticker: " + ticker));
    }

    private StockSearchResponse convertToResponse(Stock stock) {
        return new StockSearchResponse(
                stock.getTicker(),
                stock.getName(),
                stock.getCurrentPrice(),
                stock.getPer(),
                stock.getPbr()
        );
    }

    private StockListResponse.StockListItem convertToStockListItem(Stock stock) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        
        String tradeDate = stock.getTradeDate() != null ? stock.getTradeDate().format(dateFormatter) : null;
        String tradeTime = stock.getTradeTime() != null ? stock.getTradeTime().format(timeFormatter) : null;
        String currentPrice = stock.getCurrentPrice() != null ? stock.getCurrentPrice().toString() : "0";
        
        return new StockListResponse.StockListItem(
                stock.getName(),
                stock.getTicker(),
                tradeDate,
                tradeTime,
                currentPrice
        );
    }
} 