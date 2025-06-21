package com.hts.service;

import com.hts.domain.Account;
import com.hts.domain.Trade;
import com.hts.dto.TradeRequest;
import com.hts.dto.TradeResponse;
import com.hts.exception.InsufficientFundsException;
import com.hts.repository.AccountRepository;
import com.hts.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TradeService {

    private final TradeRepository tradeRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public TradeService(TradeRepository tradeRepository, AccountRepository accountRepository) {
        this.tradeRepository = tradeRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public TradeResponse buyStock(TradeRequest request) {
        try {
            // Calculate total purchase amount
            BigDecimal totalAmount = request.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
            
            // Get account and check balance
            Account account = accountRepository.findByAccountId(request.getAccountId())
                    .orElseThrow(() -> new RuntimeException("Account not found: " + request.getAccountId()));
            
            if (account.getBalance().compareTo(totalAmount) < 0) {
                throw new InsufficientFundsException(request.getAccountId(), totalAmount, account.getBalance());
            }
            
            // Deduct amount from account balance
            account.setBalance(account.getBalance().subtract(totalAmount));
            accountRepository.save(account);
            
            // Create and save trade record
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
            
            return new TradeResponse(true, tradeId, 
                String.format("Buy order executed successfully. Amount: %s, New Balance: %s", 
                             totalAmount, account.getBalance()));
            
        } catch (InsufficientFundsException e) {
            return new TradeResponse(false, null, e.getMessage());
        } catch (Exception e) {
            return new TradeResponse(false, null, "Failed to execute buy order: " + e.getMessage());
        }
    }

    @Transactional
    public TradeResponse sellStock(TradeRequest request) {
        try {
            // Calculate total sale amount
            BigDecimal totalAmount = request.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
            
            // Get account
            Account account = accountRepository.findByAccountId(request.getAccountId())
                    .orElseThrow(() -> new RuntimeException("Account not found: " + request.getAccountId()));
            
            // Add amount to account balance
            account.setBalance(account.getBalance().add(totalAmount));
            accountRepository.save(account);
            
            // Create and save trade record
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
            
            return new TradeResponse(true, tradeId, 
                String.format("Sell order executed successfully. Amount: %s, New Balance: %s", 
                             totalAmount, account.getBalance()));
            
        } catch (Exception e) {
            return new TradeResponse(false, null, "Failed to execute sell order: " + e.getMessage());
        }
    }

    private String generateTradeId() {
        return "TRADE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
} 