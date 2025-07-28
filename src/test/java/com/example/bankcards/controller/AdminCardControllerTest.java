package com.example.bankcards.controller;

import com.example.bankcards.dto.filter.CardFilter;
import com.example.bankcards.dto.request.CardTypeRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.CardType;
import com.example.bankcards.service.card.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminCardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CardService cardService;

    @InjectMocks
    private AdminCardController adminCardController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(adminCardController).build();
    }

    @Test
    void createCard_ShouldReturnCreatedCard() throws Exception {
        CardTypeRequest request = new CardTypeRequest();
        request.setId(1L);
        request.setCardType(CardType.VISA);

        CardResponse response = new CardResponse();
        response.setId(1L);
        response.setCardTypeCode(1);
        response.setCardNumber("4111111123456789");
        response.setOwnerId(1L);
        response.setExpirationDate(LocalDateTime.now());
        response.setCardStatus(CardStatus.ACTIVE);

        Mockito.when(cardService.create(eq(1L), any(CardTypeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/api/admin/cards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void changeCardStatus_ShouldReturnUpdatedCard() throws Exception {
        CardResponse response = new CardResponse();
        response.setId(1L);
        response.setCardStatus(CardStatus.BLOCKED);

        Mockito.when(cardService.changeStatus(eq(1L), eq(CardStatus.BLOCKED))).thenReturn(response);

        mockMvc.perform(post("/v1/api/admin/cards/1/status/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CardStatus.BLOCKED)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cardStatus").value("BLOCKED"));
    }

    @Test
    void deleteCard_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/v1/api/admin/cards/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(cardService).deleteCard(1L);
    }

    @Test
    void getCard_ShouldReturnCard() throws Exception {
        CardResponse response = new CardResponse();
        response.setId(1L);

        Mockito.when(cardService.findCardById(1L)).thenReturn(response);

        mockMvc.perform(get("/v1/api/admin/cards/get/card/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void blockCard_ShouldReturnBlockedCard() throws Exception {
        CardResponse response = new CardResponse();
        response.setId(1L);
        response.setCardStatus(CardStatus.BLOCKED);

        Mockito.when(cardService.blockCard(1L)).thenReturn(response);

        mockMvc.perform(post("/v1/api/admin/cards/block/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cardStatus").value("BLOCKED"));
    }

    @Test
    void getAllCards_ShouldReturnPagedCards() throws Exception {
        CardResponse card = new CardResponse();
        card.setId(1L);

        Mockito.when(cardService.getAllCards(any(CardFilter.class)))
                .thenReturn(new PageImpl<>(List.of(card), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/v1/api/admin/cards/get?offset=0&limit=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }
}
