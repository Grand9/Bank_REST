package com.example.bankcards.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private Long userId;
}