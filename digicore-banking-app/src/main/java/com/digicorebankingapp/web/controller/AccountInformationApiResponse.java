package com.digicorebankingapp.web.controller;

import com.digicorebankingapp.data.model.AccountInformation;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class AccountInformationApiResponse {
    private HttpStatus responseCode;
    private boolean success;
    private String message;
    private AccountInformation account;

    public AccountInformationApiResponse(HttpStatus responseCode, boolean success, String message, AccountInformation account) {
        this.responseCode = responseCode;
        this.success = success;
        this.message = message;
        this.account = account;
    }
}
