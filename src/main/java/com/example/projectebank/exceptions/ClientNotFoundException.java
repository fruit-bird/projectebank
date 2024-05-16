package com.example.projectebank.exceptions;

public class ClientNotFoundException extends Exception {
    public ClientNotFoundException(String clientNotFound) {
        super(clientNotFound);
    }
}
