package com.hts.repository;

import com.hts.domain.Account;
import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findByAccountId(String accountId);
    Account save(Account account);
    Account createAccount(String accountId, java.math.BigDecimal initialBalance);
} 