package com.digicorebankingapp.web.controller;

import com.digicorebankingapp.data.dto.AccountDto;
import com.digicorebankingapp.data.dto.DepositDto;
import com.digicorebankingapp.data.dto.LoginDto;
import com.digicorebankingapp.data.dto.WithdrawalDto;
import com.digicorebankingapp.data.model.AccountInformation;
import com.digicorebankingapp.data.model.Transaction;
import com.digicorebankingapp.services.BankServices;
import com.digicorebankingapp.services.UserServices;
import com.digicorebankingapp.web.exception.BankAppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/bank-app")
public class BankAppController {
    @Autowired
    BankServices bankServices;

    @Autowired
    UserServices userServices;

    @PostMapping("/register")
    public Response register(@RequestBody AccountDto accountDto) {
        String result = null;
        try {
            result = bankServices.createAccount(accountDto);
            return new Response(HttpStatus.OK, true, result);
        } catch (BankAppException e) {
            log.info("{}", e.getMessage());
            return new Response(HttpStatus.BAD_REQUEST, false, e.getMessage());
        }
    }


    @PostMapping(value = "/login")
    public Response login(@RequestBody LoginDto loginDto) {
        String token;
        try {
            bankServices.findAccountByAccountNumber(loginDto.getAccountNumber());
            token = userServices.login(loginDto).getAccessToken();
        } catch (BankAppException e) {
            return new Response(HttpStatus.BAD_REQUEST, false, e.getMessage());
        }
        return new Response(HttpStatus.OK, true, token);
    }


    @PostMapping("/deposit")
    public Response deposit(@RequestBody DepositDto depositDto) {
        String accountInformation;
        try {
            log.info("account number from deposit endpoint is ---->{}", depositDto.getAccountNumber());
            accountInformation = bankServices.deposit(depositDto.getAccountNumber(), depositDto.getAmountToDeposit());
        } catch (BankAppException exception) {
            return new Response(HttpStatus.BAD_REQUEST, false, exception.getMessage());
        }
        return new Response(HttpStatus.OK, true, accountInformation);

    }


    @PostMapping("/withdraw")
    public Response withdrawal(@RequestBody WithdrawalDto withdrawalDto) {
        String accountInformation;
        try {
            accountInformation = bankServices.withdraw(withdrawalDto.getAccountNumber(), withdrawalDto.getPassword(), withdrawalDto.getAmount());

        } catch (BankAppException exception) {
            return new Response(HttpStatus.BAD_REQUEST, false, exception.getMessage());
        }
        return new Response(HttpStatus.OK, true, accountInformation);

    }


    @GetMapping("/account_statement/{accountNumber}")
    public ResponseEntity<?> accountStatement(@RequestBody AccountDto accountDto, @PathVariable String accountNumber) {
        List<Transaction> accountStatement;
        try {
            accountStatement = bankServices.generateAccountStatement(accountNumber, accountDto.getAccountPassword());

        } catch (BankAppException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(accountStatement, HttpStatus.OK);

    }


    @GetMapping("/account_info/{accountNumber}")
    public ResponseEntity<?> accountInfo(@RequestBody AccountDto accountDto, @PathVariable String accountNumber) {
        AccountInformation accountInformation;
        try {
            accountInformation = bankServices.getAccountInformation(accountNumber, accountDto.getAccountPassword());

        } catch (BankAppException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new AccountInformationApiResponse(HttpStatus.OK, true, "successful", accountInformation), HttpStatus.OK);

    }
}