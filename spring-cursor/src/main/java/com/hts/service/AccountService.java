package com.hts.service;

import com.hts.domain.Account;
import com.hts.domain.User;
import com.hts.exception.InsufficientBalanceException;
import com.hts.repository.AccountRepository;
import com.hts.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void deductBalance(UUID accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
        
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance. Required: " + amount + ", Available: " + account.getBalance());
        }
        
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
    }

    @Transactional
    public void addBalance(UUID accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
        
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }

    public Account getAccountByUserId(UUID userId) {
        return accountRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for user: " + userId));
    }

    public Account createAccount(User user, BigDecimal initialBalance) {
        Account account = new Account(user, initialBalance);
        return accountRepository.save(account);
    }

    public BigDecimal getBalance(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
        return account.getBalance();
    }
} 