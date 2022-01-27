package com.digicorebankingapp.data.model;

import lombok.Data;

@Data
public class AccountInformation {
    private String accountNumber;
    private String accountName;
    private Double balance;
}
