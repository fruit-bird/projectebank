package com.example.projectebank.exceptions;

public class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String insufficientFundsToConductTransaction) {
        super(insufficientFundsToConductTransaction);
    }
}
