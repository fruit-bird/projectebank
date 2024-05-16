package com.example.projectebank.exceptions;

public class BankAccountNotFound extends Exception {
    public BankAccountNotFound(String bankAccountNotFound) {
        super(bankAccountNotFound);
    }
}
