package com.example.bankcards.service.jwt;

import com.example.bankcards.dto.response.TokenResponse;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.User;

public interface JwtService {

    TokenResponse generateToken(User user);

    TokenResponse regenerateToken(String token);
}