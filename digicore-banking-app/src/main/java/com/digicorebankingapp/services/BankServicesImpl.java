package com.digicorebankingapp.services;

import com.digicorebankingapp.data.dto.AccountDto;
import com.digicorebankingapp.data.model.Account;
import com.digicorebankingapp.data.model.AccountInformation;
import com.digicorebankingapp.data.model.Transaction;
import com.digicorebankingapp.data.model.TransactionType;
import com.digicorebankingapp.web.exception.BankAppException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.digicorebankingapp.data.model.TransactionType.DEPOSIT;
import static com.digicorebankingapp.data.model.TransactionType.WITHDRAW;

@Service
@Slf4j
public class BankServicesImpl implements BankServices {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    private final Map<String, Account> bankAccountRepository = new HashMap<>();

    @Override
    public String createAccount(AccountDto accountDto) throws BankAppException {
        validateAccountDto(accountDto);

        Account account = new Account();
        modelMapper.map(accountDto,account);
        account.setBalance(accountDto.getInitialDeposit());
        account.setPassword(passwordEncoder.encode(accountDto.getAccountPassword()));

        String accountNumber = String.valueOf(UUID.randomUUID().getMostSignificantBits());
        accountNumber = accountNumber.substring(1, 11);
        account.setAccountNumber(accountNumber);

        String narration = "Initial deposit for account opening process";
        saveTransaction(account, accountDto.getInitialDeposit(), narration, DEPOSIT);

        bankAccountRepository.put(account.getAccountNumber(), account);

        return accountCreationMessage(account);
    }

    @Override
    public Account findAccountByAccountName(String accountName) throws BankAppException {
        return bankAccountRepository.values().stream()
                .filter(account -> account.getAccountName().equalsIgnoreCase(accountName))
                .findFirst().orElseThrow(() -> new BankAppException("Account name does not exist"));
    }

    @Override
    public void resetBankAccountRepository() {
        bankAccountRepository.clear();
    }

    @Override
    public Account findAccountByAccountNumber(String accountNumber) {
        log.info("accounts in the repo are ---->{}", bankAccountRepository);
        log.info("accountNumber is ---->{}", accountNumber);
        Account account = bankAccountRepository.getOrDefault(accountNumber, null);
        log.info("account detail is ----> {}", account);
        if (account == null) {
            throw new BankAppException("Invalid account Number");
        }
        return account;
    }

    @Override
    public String deposit(String accountNumber, Double amount) {
        boolean invalidAmount = amount == null || amount < 1.00 || amount > 1_000_000.00;

        if (invalidAmount) {
            throw new BankAppException("Invalid amount. Amount must be between 1.00 and 1,000,000.00");
        }

        log.info("account number from deposit implementation is ---->{}", accountNumber);
        log.info("Before account the Query is done");

        Account account = findAccountByAccountNumber(accountNumber);

        log.info("After account the Query is done");

        account.setBalance(account.getBalance() + amount);
        String narration = "Account deposit of " + amount;
        saveTransaction(account, amount, narration, DEPOSIT);
        return transactionMessage(account, amount, DEPOSIT);
    }

    @Override
    public String withdraw(String accountNumber, String password, Double amount) {
        Account account = validWithdrawCredentials(accountNumber, password, amount);

        account.setBalance(account.getBalance()-amount);
        String narration = "Account withdrawal of " + amount;
        saveTransaction(account, amount, narration , WITHDRAW);

        return transactionMessage(account,amount,WITHDRAW);
    }

    @Override
    public AccountInformation getAccountInformation(String accountNumber, String accountPassword) {
        Account account = findAccountByAccountNumber(accountNumber);
        if(!passwordEncoder.matches(accountPassword, account.getPassword())){
            throw new BankAppException("Invalid password");
        }

        AccountInformation accountInformation = new AccountInformation();
        modelMapper.map(account,accountInformation);

        return accountInformation;
    }

    @Override
    public List<Transaction> generateAccountStatement(String accountNumber, String accountPassword) {
        Account account = findAccountByAccountNumber(accountNumber);
        if(!passwordEncoder.matches(accountPassword, account.getPassword())){
            throw new BankAppException("Invalid password");
        }
        return account.getTransactions();
    }

    private Account validWithdrawCredentials(String accountNumber, String password, Double amount) {
        if(amount == null || amount < 1.00){
            throw new BankAppException("Please provide a valid amount to withdraw. Amount must be greater than 1.00");
        }
        Account account = findAccountByAccountNumber(accountNumber);

        if(!passwordEncoder.matches(password, account.getPassword())){
            throw new BankAppException("Invalid password");
        }

        if(account.getBalance() - amount < 500){
            throw new BankAppException("Insufficient funds. A minimum balance of 500 must be left in your account");
        }
        return account;
    }



    private String transactionMessage(Account account, Double amount, TransactionType transaction) {
        return String.format("You have successfully %s %.2f. Your new account balance is %.2f", transaction.toString(), amount, account.getBalance());
    }


    private String accountCreationMessage(Account account) {
        return "Dear " + account.getAccountName() + ", your account has been successfully created. " +
                "Your account number is " + account.getAccountNumber() +
                ". Account balance is " + account.getBalance();
    }

    private void validateAccountDto(AccountDto accountDto) {
        boolean invalidAccountName = accountDto.getAccountName() == null ||
                accountDto.getAccountName().isEmpty() ||
                accountDto.getAccountName().isBlank();
        if (invalidAccountName) {
            throw new BankAppException("Please provide a valid account name");
        }

        boolean invalidPassword = accountDto.getAccountPassword() == null ||
                accountDto.getAccountPassword().isEmpty() ||
                accountDto.getAccountPassword().isBlank();

        if (invalidPassword) {
            throw new BankAppException("Please provide a valid password");
        }

        boolean accountExists = isExisting(accountDto.getAccountName());
        if (accountExists) {
            throw new BankAppException("Account with the account name already exists");
        }

        if (accountDto.getInitialDeposit() < 500.00) {
            throw new BankAppException("Initial deposit should not be less than 500");
        }
    }

    private boolean isExisting(String accountName) {
        return bankAccountRepository.values().stream().anyMatch(account -> account.getAccountName().equalsIgnoreCase(accountName));
    }

    private void saveTransaction(Account account, Double amount, String narration, TransactionType transactionType){
        Transaction transaction = new Transaction();
        transaction.setTransactionDate(LocalDate.now());
        transaction.setTransactionType(transactionType);

        transaction.setNarration(narration);
        transaction.setAmount(amount);
        transaction.setAccountBalance(account.getBalance());

        account.getTransactions().add(transaction);

    }
}
