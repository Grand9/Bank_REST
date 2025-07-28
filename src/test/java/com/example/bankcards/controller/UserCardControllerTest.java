package com.example.bankcards.controller;

import com.example.bankcards.dto.filter.CardFilter;
import com.example.bankcards.dto.request.CardTypeRequest;
import com.example.bankcards.dto.response.BalanceResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.response.TransferResponse;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.CardType;
import com.example.bankcards.entity.enums.TransferStatus;
import com.example.bankcards.service.card.CardService;
import com.example.bankcards.service.transfer.TransferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserCardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CardService cardService;

    @Mock
    private TransferService transferService;

    private ObjectMapper objectMapper;

    @InjectMocks
    private UserCardController userCardController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(userCardController).build();
    }

    @Test
    void createCard_ShouldReturnCreatedCard() throws Exception {
        CardTypeRequest request = new CardTypeRequest();
        CardResponse response = new CardResponse();
        response.setId(1L);
        response.setCardTypeCode(1);
        response.setCardNumber("4111111123456789");
        response.setOwnerId(1L);
        response.setExpirationDate(LocalDateTime.of(2020, 10, 10, 10, 10));
        response.setCardStatus(CardStatus.ACTIVE);
        response.setBalance(BigDecimal.valueOf(100));
        request.setId(1L);
        request.setCardType(CardType.VISA);

        Mockito.when(cardService.create(eq(1L), any(CardTypeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/api/user/cards/create/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));

    }

    @Test
    void setCardStatus_ShouldReturnUpdatedCard() throws Exception {
        CardResponse response = new CardResponse();
        response.setId(1L);
        Mockito.when(cardService.changeStatus(eq(1L), eq(CardStatus.BLOCKED))).thenReturn(response);

        mockMvc.perform(post("/v1/api/user/cards/1/status/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CardStatus.BLOCKED)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void deleteCard_ShouldCallService() throws Exception {
        mockMvc.perform(delete("/v1/api/user/cards//1/delete"))
                .andExpect(status().isOk());

        Mockito.verify(cardService).deleteCard(1L);
    }

    @Test
    void findCardById_ShouldReturnCard() throws Exception {
        CardResponse response = new CardResponse();
        response.setId(1L);

        Mockito.when(cardService.findCardById(1L)).thenReturn(response);

        mockMvc.perform(get("/v1/api/user/cards/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void blockCard_ShouldReturnBlockedCard() throws Exception {
        CardResponse response = new CardResponse();
        response.setId(1L);

        Mockito.when(cardService.blockCard(1L)).thenReturn(response);

        mockMvc.perform(put("/v1/api/user/cards/request-block/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getBalance_ShouldReturnBalance() throws Exception {
        BalanceResponse balance = new BalanceResponse();
        balance.setBalance(BigDecimal.valueOf(1000));

        Mockito.when(cardService.getBalance(1L)).thenReturn(balance);

        mockMvc.perform(get("/v1/api/user/cards/1/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1000));
    }

    @Test
    void createTransfer_ShouldReturnTransferResponse() throws Exception {
        TransferResponse response = new TransferResponse();
        response.setId(1L);
        response.setAmount(BigDecimal.valueOf(1000));
        response.setFromCardId(2L);
        response.setToCardId(3L);
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());
        response.setStatus(TransferStatus.COMPLETED);

        Mockito.when(transferService.transfer(any(TransferResponse.class))).thenReturn(response);

        mockMvc.perform(post("/v1/api/user/cards/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getCards_ShouldReturnCardList() throws Exception {
        CardResponse card = new CardResponse();
        card.setId(1L);
        Mockito.when(cardService.getAllCards(any(CardFilter.class)))
                .thenReturn(new PageImpl<>(List.of(card), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/v1/api/user/cards/get?offset=0&limit=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    void cancelTransfer_ShouldSucceed() throws Exception {
        TransferResponse request = new TransferResponse();
        request.setId(1L);
        request.setFromCardId(2L);
        request.setToCardId(3L);
        request.setAmount(BigDecimal.valueOf(1000));
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        request.setStatus(TransferStatus.FAILED);

        mockMvc.perform(delete("/v1/api/user/cards/transfer/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Mockito.verify(transferService).cancelTransfer(any(TransferResponse.class));
    }
}