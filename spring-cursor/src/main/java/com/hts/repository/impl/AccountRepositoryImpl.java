package com.hts.repository.impl;

import com.hts.domain.Account;
import com.hts.repository.AccountRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final ConcurrentMap<String, Account> accountStore = new ConcurrentHashMap<>();

    public AccountRepositoryImpl() {
        // Initialize with some sample accounts
        createAccount("ACC001", new BigDecimal("1000000")); // 1M won
        createAccount("ACC002", new BigDecimal("500000"));  // 500K won
    }

    @Override
    public Optional<Account> findByAccountId(String accountId) {
        return Optional.ofNullable(accountStore.get(accountId));
    }

    @Override
    public Account save(Account account) {
        accountStore.put(account.getAccountId(), account);
        return account;
    }

    @Override
    public Account createAccount(String accountId, BigDecimal initialBalance) {
        Account account = new Account(accountId, initialBalance);
        accountStore.put(accountId, account);
        return account;
    }
} 