package com.hts.trade;

import java.util.List;
import java.util.Optional;

public interface TradeRepository {
    Trade save(Trade trade);
    Optional<Trade> findByTradeId(String tradeId);
    List<Trade> findByAccountId(String accountId);
    List<Trade> findByAccountIdAndTicker(String accountId, String ticker);
} 