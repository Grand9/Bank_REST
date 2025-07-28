package com.example.bankcards.service;

import com.example.bankcards.dto.filter.CardFilter;
import com.example.bankcards.dto.request.CardTypeRequest;
import com.example.bankcards.dto.response.BalanceResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.CardType;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.card.CardNumberGenerate;
import com.example.bankcards.service.card.CardServiceImpl;
import com.example.bankcards.util.mapper.CardMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardNumberGenerate cardNumberGenerate;

    @Mock
    private CardMapper cardMapper;

    private Card card;
    private User user;
    private CardResponse cardResponse;

    @BeforeEach
    public void setUp() {
        user = User.builder()
            .id(1L)
            .username("tester")
            .role(Role.USER)
            .build();

        card = Card.builder()
                .id(1L)
                .cardNumber("4111111234567890")
                .owner(user)
                .cardType(CardType.VISA)
                .cardStatus(CardStatus.ACTIVE)
                .expirationDate(LocalDateTime.now().plusYears(5))
                .balance(BigDecimal.ZERO)
                .isDeleted(false)
                .build();
        cardResponse = new CardResponse(); // Заполни нужные поля
    }

    @Test
    void create_shouldCreateCard() {
        CardTypeRequest typeRequest = new CardTypeRequest();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardNumberGenerate.generate(any(), any(), any())).thenReturn("4111111234567890");
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toDto(any())).thenReturn(cardResponse);

        CardResponse result = cardService.create(1L, typeRequest);

        assertEquals(cardResponse, result);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void changeStatus_shouldChangeCardStatus() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toDto(any())).thenReturn(cardResponse);

        CardResponse result = cardService.changeStatus(1L, CardStatus.BLOCKED);

        assertEquals(cardResponse, result);
        verify(cardRepository).save(card);
    }

    @Test
    void deleteCard_shouldMarkCardAsDeleted() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        cardService.deleteCard(1L);

        assertTrue(card.isDeleted());
        verify(cardRepository).save(card);
    }

    @Test
    void findCardById_shouldReturnCard() {
        mockSecurityContext();

        when(userRepository.findByUsername("tester")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardMapper.toDto(any())).thenReturn(cardResponse);

        CardResponse result = cardService.findCardById(1L);

        assertEquals(cardResponse, result);
    }

    @Test
    void blockCard_shouldThrowIfCardActive() {
        mockSecurityContext();
        card.setCardStatus(CardStatus.ACTIVE);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> cardService.blockCard(1L));
        assertEquals("Active card can be blocked", ex.getMessage());
    }

    @Test
    void getBalance_shouldReturnCardBalance() {
        mockSecurityContext();

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        BalanceResponse result = cardService.getBalance(1L);

        assertEquals(BigDecimal.ZERO, result.getBalance());
    }

    @Test
    void getAllCards_shouldReturnPageOfCards() {
        Page<Card> page = new PageImpl<>(List.of(card));
        when(cardRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(cardMapper.toDto(any())).thenReturn(cardResponse);

        CardFilter filter = new CardFilter(0, 10);
        Page<CardResponse> result = cardService.getAllCards(filter);

        assertEquals(1, result.getContent().size());
        verify(cardRepository).findAll(any(PageRequest.class));
    }

    private void mockSecurityContext() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("tester");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }
}