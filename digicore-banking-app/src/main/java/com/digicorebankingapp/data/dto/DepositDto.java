package com.digicorebankingapp.data.dto;


import lombok.Data;

@Data
public class DepositDto {
    private String accountNumber;
    private Double amountToDeposit;
}
