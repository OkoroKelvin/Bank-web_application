package com.digicorebankingapp.services;

import com.digicorebankingapp.data.dto.AccountDto;
import com.digicorebankingapp.data.model.Account;
import com.digicorebankingapp.data.model.AccountInformation;
import com.digicorebankingapp.data.model.Transaction;
import com.digicorebankingapp.web.exception.BankAppException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BankServices {
    String createAccount(AccountDto accountDto) throws BankAppException;

    Account findAccountByAccountName(String accountName) throws BankAppException;

    void resetBankAccountRepository();

    Account findAccountByAccountNumber(String accountNumber);

    String deposit(String accountNumber, Double amount);

    String withdraw(String accountNumber, String password, Double amount);

    AccountInformation getAccountInformation(String accountNumber, String accountPassword);

    List<Transaction> generateAccountStatement(String accountNumber, String accountPassword);
}
