package com.digicorebankingapp.data.dto;


import lombok.Data;

@Data
public class AccountDto {
    private String accountName;
    private String accountPassword;
    private Double initialDeposit;
    private String accountNumber;
}
