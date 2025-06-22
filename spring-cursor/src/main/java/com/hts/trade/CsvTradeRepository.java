package com.hts.trade;

import com.hts.account.Account;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class CsvTradeRepository implements TradeRepository {

    private final ConcurrentMap<String, Trade> tradeStore = new ConcurrentHashMap<>();

    @Override
    public Trade save(Trade trade) {
        tradeStore.put(trade.getTradeId(), trade);
        return trade;
    }

    @Override
    public Optional<Trade> findByTradeId(String tradeId) {
        return Optional.ofNullable(tradeStore.get(tradeId));
    }

    @Override
    public List<Trade> findByAccountId(String accountId) {
        return tradeStore.values().stream()
                .filter(trade -> {
                    Account account = trade.getAccount();
                    return account != null && account.getId().toString().equals(accountId);
                })
                .toList();
    }

    @Override
    public List<Trade> findByAccountIdAndTicker(String accountId, String ticker) {
        return tradeStore.values().stream()
                .filter(trade -> {
                    Account account = trade.getAccount();
                    return account != null && account.getId().toString().equals(accountId) &&
                           trade.getTicker().equals(ticker);
                })
                .toList();
    }
} 