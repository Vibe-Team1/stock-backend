package com.hts.repository;

import com.hts.domain.Trade;
import java.util.List;
import java.util.Optional;

public interface TradeRepository {
    Trade save(Trade trade);
    Optional<Trade> findByTradeId(String tradeId);
    List<Trade> findByAccountId(String accountId);
    List<Trade> findByAccountIdAndTicker(String accountId, String ticker);
} 