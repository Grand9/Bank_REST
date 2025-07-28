package com.example.bankcards.controller;

import com.example.bankcards.dto.request.LoginRequest;
import com.example.bankcards.dto.request.RegistrationRequestDto;
import com.example.bankcards.dto.response.TokenResponse;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.service.authentication.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping("v1/api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Registration user",
            description = "Registration a new user",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration request payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegistrationRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "User successfully registration"),
                    @ApiResponse(responseCode = "400", description = "Invalid request payload")
            }
    )
    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse registration(@RequestBody @Valid RegistrationRequestDto registrationRequestDto) {
        log.info("Registration request: {}", registrationRequestDto);
        return authenticationService.registration(registrationRequestDto);
    }

    @Operation(
            summary = "Login user",
            description = "Login a user",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User login request payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "User successfully login"),
                    @ApiResponse(responseCode = "400", description = "Invalid request payload")
            }
    )
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        log.info("Login request: {}", loginRequest);
        return authenticationService.login(loginRequest);
    }

    @Operation(
            summary = "Logout user",
            description = "Logout a user",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User logout request payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegistrationRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "User successfully logout"),
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        log.info("Logout user");
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .path("/v1/api/auth/refresh_token")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
    @Operation(
            summary = "Generate token",
            description = "Generates new access/refresh token",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Token successfully refresh"),
            }
    )
    @PostMapping("/refresh_token")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponse refreshToken(@RequestParam String refreshToken) {
        log.info("Refresh token: {}", refreshToken);
        return authenticationService.refreshToken(refreshToken);
    }
}