package com.example.projectebank.dtos;

import lombok.Data;

@Data
public class CreditDTO {
    private String accountID;
    private double amount;
    private String description;
}
