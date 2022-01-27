package com.digicorebankingapp.data.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Transaction {
    private LocalDate transactionDate;
    private TransactionType transactionType;
    private String narration;
    private Double amount;
    private Double accountBalance;
}
