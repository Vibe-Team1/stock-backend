package com.hts.exception;

public class InsufficientFundsException extends RuntimeException {
    
    public InsufficientFundsException(String message) {
        super(message);
    }
    
    public InsufficientFundsException(String accountId, java.math.BigDecimal required, java.math.BigDecimal available) {
        super(String.format("Insufficient funds for account %s. Required: %s, Available: %s", 
                           accountId, required, available));
    }
} 