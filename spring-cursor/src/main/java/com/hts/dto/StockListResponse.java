package com.hts.dto;

import com.hts.domain.Stock;

import java.util.List;

public class StockListResponse {
    private List<StockListItem> stocks;
    private PaginationInfo pagination;

    public StockListResponse() {}

    public StockListResponse(List<StockListItem> stocks, PaginationInfo pagination) {
        this.stocks = stocks;
        this.pagination = pagination;
    }

    // Getters and Setters
    public List<StockListItem> getStocks() {
        return stocks;
    }

    public void setStocks(List<StockListItem> stocks) {
        this.stocks = stocks;
    }

    public PaginationInfo getPagination() {
        return pagination;
    }

    public void setPagination(PaginationInfo pagination) {
        this.pagination = pagination;
    }

    public static class StockListItem {
        private String name;
        private String ticker;
        private String tradeDate;
        private String tradeTime;
        private String currentPrice;

        public StockListItem() {}

        public StockListItem(String name, String ticker, String tradeDate, String tradeTime, String currentPrice) {
            this.name = name;
            this.ticker = ticker;
            this.tradeDate = tradeDate;
            this.tradeTime = tradeTime;
            this.currentPrice = currentPrice;
        }

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTicker() {
            return ticker;
        }

        public void setTicker(String ticker) {
            this.ticker = ticker;
        }

        public String getTradeDate() {
            return tradeDate;
        }

        public void setTradeDate(String tradeDate) {
            this.tradeDate = tradeDate;
        }

        public String getTradeTime() {
            return tradeTime;
        }

        public void setTradeTime(String tradeTime) {
            this.tradeTime = tradeTime;
        }

        public String getCurrentPrice() {
            return currentPrice;
        }

        public void setCurrentPrice(String currentPrice) {
            this.currentPrice = currentPrice;
        }
    }

    public static class PaginationInfo {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;

        public PaginationInfo() {}

        public PaginationInfo(int page, int size, long totalElements, int totalPages) {
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
        }

        // Getters and Setters
        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(long totalElements) {
            this.totalElements = totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }
    }
} 