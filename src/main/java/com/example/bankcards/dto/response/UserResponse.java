package com.example.bankcards.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    @NotNull
    private long id;

    @NotNull
    private String username;

    private String password;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String phone;

    @NotNull
    private boolean isBanned;

    @NotNull
    private String role;
}
