package com.digicorebankingapp.services;

import com.digicorebankingapp.data.dto.LoginDto;
import com.digicorebankingapp.data.model.AccessToken;
import com.digicorebankingapp.data.model.Account;
import com.digicorebankingapp.security.TokenServices;
import com.digicorebankingapp.web.exception.BankAppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServicesImpl  implements UserServices{

    @Autowired
    private BankServices bankServices;

    @Autowired
    private TokenServices tokenServices;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BankUserDetailsService userDetailsService;


    @Override
    public AccessToken login(LoginDto loginDto) {
        Account account = bankServices.findAccountByAccountNumber(loginDto.getAccountNumber());
        if(!passwordEncoder.matches(loginDto.getPassword(),account.getPassword())){
            throw new BankAppException("Invalid password");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getAccountNumber());
        String token = tokenServices.generateToken(userDetails);
        return new AccessToken(token);
    }
}
