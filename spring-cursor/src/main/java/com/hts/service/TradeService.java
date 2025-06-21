package com.hts.service;

import com.hts.domain.Trade;
import com.hts.dto.TradeRequest;
import com.hts.dto.TradeResponse;
import com.hts.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TradeService {

    private final TradeRepository tradeRepository;

    @Autowired
    public TradeService(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    public TradeResponse buyStock(TradeRequest request) {
        try {
            String tradeId = generateTradeId();
            Trade trade = new Trade(
                    tradeId,
                    request.getAccountId(),
                    request.getTicker(),
                    request.getPrice(),
                    request.getQuantity(),
                    request.getTimestamp(),
                    Trade.TradeType.BUY
            );
            
            tradeRepository.save(trade);
            
            return new TradeResponse(true, tradeId, "Buy order executed successfully");
        } catch (Exception e) {
            return new TradeResponse(false, null, "Failed to execute buy order: " + e.getMessage());
        }
    }

    public TradeResponse sellStock(TradeRequest request) {
        try {
            String tradeId = generateTradeId();
            Trade trade = new Trade(
                    tradeId,
                    request.getAccountId(),
                    request.getTicker(),
                    request.getPrice(),
                    request.getQuantity(),
                    request.getTimestamp(),
                    Trade.TradeType.SELL
            );
            
            tradeRepository.save(trade);
            
            return new TradeResponse(true, tradeId, "Sell order executed successfully");
        } catch (Exception e) {
            return new TradeResponse(false, null, "Failed to execute sell order: " + e.getMessage());
        }
    }

    private String generateTradeId() {
        return "TRADE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
} 