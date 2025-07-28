package com.example.bankcards.controller;

import com.example.bankcards.dto.request.LoginRequest;
import com.example.bankcards.dto.request.RegistrationRequestDto;
import com.example.bankcards.dto.response.TokenResponse;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.service.authentication.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationService authenticationService;

    private ObjectMapper objectMapper;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
    }

    @Test
    void registration_ShouldReturnCreatedUser() throws Exception {
        RegistrationRequestDto request = new RegistrationRequestDto();
        request.setUsername("tester");
        request.setPassword("password");
        request.setEmail("test@example.com");

        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setUsername("tester");

        Mockito.when(authenticationService.registration(any(RegistrationRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/v1/api/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("tester"));
    }

    @Test
    void login_ShouldReturnTokenResponse() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("tester");
        loginRequest.setPassword("password");

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken("access-token");
        tokenResponse.setRefreshToken("refresh-token");

        Mockito.when(authenticationService.login(any(LoginRequest.class))).thenReturn(tokenResponse);

        mockMvc.perform(post("/v1/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void logout_ShouldClearRefreshCookie() throws Exception {
        mockMvc.perform(post("/v1/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("refresh_token=")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Max-Age=0")));
    }

    @Test
    void refreshToken_ShouldReturnNewTokens() throws Exception {
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken("new-access-token");
        tokenResponse.setRefreshToken("new-refresh-token");

        Mockito.when(authenticationService.refreshToken(eq("refresh-token")))
                .thenReturn(tokenResponse);

        mockMvc.perform(post("/v1/api/auth/refresh_token")
                        .param("refreshToken", "refresh-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"));
    }

    @Test
    void registration_ShouldFail_WhenUsernameIsBlank() throws Exception {
        RegistrationRequestDto request = new RegistrationRequestDto();
        request.setUsername("");
        request.setPassword("password");
        request.setEmail("test@example.com");

        mockMvc.perform(post("/v1/api/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registration_ShouldFail_WhenEmailInvalid() throws Exception {
        RegistrationRequestDto request = new RegistrationRequestDto();
        request.setUsername("tester");
        request.setPassword("password");
        request.setEmail("invalid-email");

        mockMvc.perform(post("/v1/api/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldFail_WhenPasswordBlank() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("tester");
        loginRequest.setPassword(""); // пустой

        mockMvc.perform(post("/v1/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refreshToken_ShouldFail_WhenTokenMissing() throws Exception {
        mockMvc.perform(post("/v1/api/auth/refresh_token"))
                .andExpect(status().isBadRequest());
    }
}