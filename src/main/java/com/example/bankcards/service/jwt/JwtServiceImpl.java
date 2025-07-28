package com.example.bankcards.service.jwt;

import com.example.bankcards.dto.response.TokenResponse;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.Token;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.InvalidTokenException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.TokenRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JwtServiceImpl implements JwtService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final TokenRepository tokenRepository;

    @Override
    public TokenResponse generateToken(User user) {
        log.debug("Generating token");
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);
        saveToken(user.getUsername(), refreshToken);
        log.debug("Generated token");
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    @Override
    public TokenResponse regenerateToken(String token) {
        log.debug("Regenerating token");
        if(!jwtProvider.validateToken(token)) {
            throw new InvalidTokenException("Invalid token");
        }
        String username = jwtProvider.extractAllClaims(token).getSubject();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found" + username));
        log.debug("Regenerated token");
        return generateToken(user);
    }

    private void saveToken(String username, String token) {
        log.debug("Saving token");
        if(!userRepository.existsByUsername(username)) {
            throw new UserNotFoundException("User not found" + username);
        }
        tokenRepository.save(new Token(username, token));
    }
}