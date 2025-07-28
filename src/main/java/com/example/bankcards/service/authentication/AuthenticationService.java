package com.example.bankcards.service.authentication;

import com.example.bankcards.dto.request.LoginRequest;
import com.example.bankcards.dto.request.RegistrationRequestDto;
import com.example.bankcards.dto.response.TokenResponse;
import com.example.bankcards.dto.response.UserResponse;

import java.net.URI;

public interface AuthenticationService {

    TokenResponse login(LoginRequest loginRequest);

    UserResponse registration (RegistrationRequestDto registrationRequestDto);

    TokenResponse refreshToken(String refreshToken);
}