package com.hts.trade;

import com.hts.account.Account;
import com.hts.account.AccountService;
import com.hts.stock.Stock;
import com.hts.stock.StockService;
import com.hts.user.UserStockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TradeService {

    private final TradeRepository tradeRepository;
    private final AccountService accountService;
    private final StockService stockService;
    private final UserStockRepository userStockRepository;

    public TradeService(TradeRepository tradeRepository, AccountService accountService, StockService stockService, UserStockRepository userStockRepository) {
        this.tradeRepository = tradeRepository;
        this.accountService = accountService;
        this.stockService = stockService;
        this.userStockRepository = userStockRepository;
    }

    @Transactional
    public TradeResponse buyStock(TradeRequest request) {
        try {
            // Validate stock exists
            Stock stock = stockService.getStockByTicker(request.getTicker());

            // Calculate total cost
            BigDecimal totalCost = request.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

            // Deduct balance from account
            accountService.deductBalance(UUID.fromString(request.getAccountId()), totalCost);

            // Create and save trade
            String tradeId = generateTradeId();
            Account account = accountService.getAccountByUserId(UUID.fromString(request.getAccountId()));

            Trade trade = new Trade(
                    tradeId,
                    account,
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

    @Transactional
    public TradeResponse sellStock(TradeRequest request) {
        try {
            // Validate stock exists
            Stock stock = stockService.getStockByTicker(request.getTicker());

            // Calculate total proceeds
            BigDecimal totalProceeds = request.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

            // Add proceeds to account
            accountService.addBalance(UUID.fromString(request.getAccountId()), totalProceeds);

            // Create and save trade
            String tradeId = generateTradeId();
            Account account = accountService.getAccountByUserId(UUID.fromString(request.getAccountId()));

            Trade trade = new Trade(
                    tradeId,
                    account,
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