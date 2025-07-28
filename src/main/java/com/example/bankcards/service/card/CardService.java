package com.example.bankcards.service.card;

import com.example.bankcards.dto.filter.CardFilter;
import com.example.bankcards.dto.request.CardTypeRequest;
import com.example.bankcards.dto.response.BalanceResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.enums.CardStatus;
import org.springframework.data.domain.Page;

public interface CardService {

    CardResponse create(Long ownerId, CardTypeRequest cardResponse);

    CardResponse changeStatus(Long ownerId, CardStatus cardStatus);

    void deleteCard(Long cardId);

    CardResponse findCardById(Long cardId);

    CardResponse blockCard(Long cardId);

    BalanceResponse getBalance(Long cardId);

    Page<CardResponse> getAllCards(CardFilter cardFilter);

}