package com.digicorebankingapp.data.dto;

import lombok.Data;

@Data
public class WithdrawalDto {
    private String accountNumber;
    private String password;
    private Double amount;
}
