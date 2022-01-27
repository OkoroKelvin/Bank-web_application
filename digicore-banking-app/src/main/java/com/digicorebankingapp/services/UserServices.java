package com.digicorebankingapp.services;

import com.digicorebankingapp.data.dto.LoginDto;
import com.digicorebankingapp.data.model.AccessToken;

public interface UserServices {
    AccessToken login(LoginDto loginDto);
}
