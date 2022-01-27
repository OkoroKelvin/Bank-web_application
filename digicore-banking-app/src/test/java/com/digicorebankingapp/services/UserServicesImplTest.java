package com.digicorebankingapp.services;

import com.digicorebankingapp.data.dto.AccountDto;
import com.digicorebankingapp.data.dto.LoginDto;
import com.digicorebankingapp.data.model.AccessToken;
import com.digicorebankingapp.web.exception.BankAppException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class UserServicesImplTest {

    @Autowired
    UserServices userServices;

    @Autowired
    BankServices bankServices;

    AccountDto accountDto;

    LoginDto loginDto;

    @BeforeEach
    void setUp() {
        accountDto = new AccountDto();
        accountDto.setAccountName("Kelvin Okoro");
        accountDto.setAccountPassword("1234");
        accountDto.setInitialDeposit(1000.00);
        loginDto = new LoginDto();
    }

    @AfterEach
    void tearDown() {
        bankServices.resetBankAccountRepository();
    }

    @Test
    void testThatLoginGeneratesToken(){
        bankServices.createAccount(accountDto);
        String accountNumber = bankServices.findAccountByAccountName("Kelvin Okoro").getAccountNumber();
        loginDto.setAccountNumber(accountNumber);
        loginDto.setPassword("1234");
        AccessToken token = userServices.login(loginDto);

        log.info("Token --> {}", token);
        assertNotNull(token.getAccessToken());
    }

    @Test
    void testThatInvalidPasswordThrowsBankAppException(){
        bankServices.createAccount(accountDto);
        String accountNumber = bankServices.findAccountByAccountName("Kelvin Okoro").getAccountNumber();

        loginDto.setAccountNumber(accountNumber);
        loginDto.setPassword("234533");
        assertThrows(BankAppException.class, ()-> userServices.login(loginDto));
    }
}