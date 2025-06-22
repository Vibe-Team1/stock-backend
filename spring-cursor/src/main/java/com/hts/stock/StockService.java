// Service for handling stock-related operations
package com.hts.stock;

import com.hts.stock.Stock;
import com.hts.stock.StockListResponse;
import com.hts.stock.StockSearchResponse;
import com.hts.stock.StockNotFoundException;
import com.hts.stock.StockRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        Pageable pageable = PageRequest.of(page, size);
        Page<Stock> stockPage = stockRepository.findAll(pageable);

        List<StockListResponse.StockListItem> stockItems = stockPage.getContent().stream()
                .map(this::convertToStockListItem)
                .collect(Collectors.toList());

        StockListResponse.PaginationInfo paginationInfo = new StockListResponse.PaginationInfo(
                stockPage.getNumber(),
                stockPage.getSize(),
                stockPage.getTotalElements(),
                stockPage.getTotalPages()
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