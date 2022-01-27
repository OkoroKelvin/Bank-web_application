package com.digicorebankingapp.services;

import com.digicorebankingapp.data.dto.AccountDto;
import com.digicorebankingapp.data.model.Account;
import com.digicorebankingapp.data.model.AccountInformation;
import com.digicorebankingapp.data.model.Transaction;
import com.digicorebankingapp.web.exception.BankAppException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Slf4j
class BankServicesImplTest {

    @Autowired
    BankServicesImpl bankServices;

    AccountDto accountDto;

    @BeforeEach
    void setUp() {
        accountDto = new AccountDto();
        accountDto.setAccountName("Kelvin Okoro");
        accountDto.setAccountPassword("1234");
    }

    @AfterEach
    void tearDown() {
        accountDto = null;
        bankServices.resetBankAccountRepository();
    }

    @Test
    void testThatAccountCanBeCreated(){
        accountDto.setInitialDeposit(1000.00);
        String message = null;
        message = bankServices.createAccount(accountDto);

        log.info("{}", message);

        Account account = bankServices.findAccountByAccountName("Kelvin Okoro");

        assertEquals(account.getAccountName(), accountDto.getAccountName());
        assertEquals(account.getBalance(), accountDto.getInitialDeposit());
        assertEquals(10, account.getAccountNumber().length());
    }

    @Test
    void testToFindAccountByItsAccountNumber(){
        accountDto.setInitialDeposit(1000.00);
        bankServices.createAccount(accountDto);
        Account account = bankServices.findAccountByAccountName("Kelvin Okoro");
        Account account1 = bankServices.findAccountByAccountNumber(account.getAccountNumber());
        assertEquals(account,account1);
    }

    @Test
    void testThatMoneyCanBeDepositedToBankAccount(){
        accountDto.setInitialDeposit(1000.0);
        bankServices.createAccount(accountDto);
        Account account = bankServices.findAccountByAccountName("Kelvin Okoro");
        assertEquals(1000.0, account.getBalance());

        String message = bankServices.deposit(account.getAccountNumber(), 5000.0);
        log.info(message);
        assertEquals(6000.0,account.getBalance());
    }

    @Test
    void testThatMoneyCanBeWithdrawFromAccount(){
        accountDto.setInitialDeposit(590000.0);
        assertEquals(590000.0, accountDto.getInitialDeposit());
        bankServices.createAccount(accountDto);
        Account account = bankServices.findAccountByAccountName("Kelvin Okoro");
        assertEquals(590000.0, account.getBalance());

        String message = bankServices.withdraw(account.getAccountNumber(), "1234", 90000.0);
        log.info(message);
        assertEquals(500000.0,account.getBalance());
    }

    @Test
    void testToGenerateAccountStatement(){
        accountDto.setInitialDeposit(10000.0);
        assertEquals(10000.0, accountDto.getInitialDeposit());
        bankServices.createAccount(accountDto);
        Account account = bankServices.findAccountByAccountName("Kelvin Okoro");
        assertEquals(10000.0, account.getBalance());
        assertEquals(1, account.getTransactions().size());


        bankServices.deposit(account.getAccountNumber(), 5000.0);
        bankServices.deposit(account.getAccountNumber(), 5000.0);
        bankServices.withdraw(account.getAccountNumber(), "1234", 2500.0);
        bankServices.withdraw(account.getAccountNumber(), "1234", 2500.0);


        List<Transaction> accountStatements = bankServices.generateAccountStatement(account.getAccountNumber(), accountDto.getAccountPassword());

        assertEquals(5, accountStatements.size());
        assertEquals(15000.0, account.getBalance());
    }

    @Test
    void testThatDepositAmountLessThanOneThrowBankAppException(){
        accountDto.setInitialDeposit(1000.0);
        bankServices.createAccount(accountDto);
        Account account = bankServices.findAccountByAccountName("Kelvin Okoro");
        assertThrows(BankAppException.class, () -> bankServices.deposit(account.getAccountNumber(), -2000.0));
    }
    @Test
    void testThatInvalidWithDrawAccountNumberThrowsBankAppExceptionError(){
        accountDto.setInitialDeposit(1000.0);
        bankServices.createAccount(accountDto);
        assertThrows(BankAppException.class, () -> bankServices.withdraw("12448787879","1234",500.00 ));
    }

    @Test
    void testThatInvalidWithdrawPasswordThrowsBankAppException(){
        accountDto.setInitialDeposit(1000.0);
        bankServices.createAccount(accountDto);
        Account account = bankServices.findAccountByAccountName("Kelvin Okoro");
        assertThrows(BankAppException.class, () -> bankServices.withdraw(account.getAccountNumber(), "xyzt", 5000.0));
    }
    @Test
    void testThatInvalidWithDrawAmountThrowsBankAppException(){
        accountDto.setInitialDeposit(1000.0);
        bankServices.createAccount(accountDto);
        Account account = bankServices.findAccountByAccountName("Kelvin Okoro");
        assertThrows(BankAppException.class, () -> bankServices.withdraw(account.getAccountNumber(), "1234", 700.0));
    }

    @Test
    void testForAccountInformation() {
        accountDto.setInitialDeposit(2000.0);
        bankServices.createAccount(accountDto);
        Account account = bankServices.findAccountByAccountName("Kelvin Okoro");

        accountDto.setAccountNumber(account.getAccountNumber());
        AccountInformation accountInfo = bankServices.getAccountInformation(accountDto.getAccountNumber(), accountDto.getAccountPassword());

        assertEquals(accountInfo.getAccountNumber(), account.getAccountNumber());
        assertEquals(accountInfo.getAccountName(), account.getAccountName());

    }
}
