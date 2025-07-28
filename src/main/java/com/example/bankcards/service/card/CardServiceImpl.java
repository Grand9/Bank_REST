package com.example.bankcards.service.card;

import com.example.bankcards.dto.filter.CardFilter;
import com.example.bankcards.dto.request.CardTypeRequest;
import com.example.bankcards.dto.response.BalanceResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.CardType;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.mapper.CardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final UserRepository userRepository;
    private final CardNumberGenerate cardNumberGenerate;


    @Override
    public CardResponse create(Long ownerId, CardTypeRequest cardTypeRequest) {
        log.debug("Create Card");
        User user = userRepository.findById(ownerId).orElseThrow(() -> new UserNotFoundException("User not found" + ownerId));
        CardType cardType = cardTypeRequest.getCardType();
        String cardNumberCreate = Card.builder().build().getCardNumber();
        String cardNumber = cardNumberGenerate.generate(cardType, ownerId, cardNumberCreate);
        LocalDateTime expiration = LocalDateTime.now().plusYears(5);
        Card card = Card.builder()
                .cardNumber(cardNumber)
                .cardStatus(CardStatus.ACTIVE)
                .owner(user)
                .expirationDate(expiration)
                .isDeleted(false)
                .cardType(cardType)
                .balance(BigDecimal.ZERO)
                .build();
        Card savedCard = cardRepository.save(card);
        log.debug("Card created");
        return cardMapper.toDto(savedCard);
    }

    @Override
    public CardResponse changeStatus(Long ownerId, CardStatus cardStatus) {
        log.debug("Change Card Status");
        User user = userRepository.findById(ownerId).orElseThrow(() -> new UserNotFoundException("User not found" + ownerId));
        Card card = getBankCard(ownerId);
        card.setCardStatus(cardStatus);

        card = cardRepository.save(card);

        log.debug("Card changed");
        return cardMapper.toDto(card);
    }

    @Override
    public void deleteCard(Long cardId) {
        log.debug("Delete Card");
        Card card = getBankCard(cardId);
        card.setDeleted(true);
        cardRepository.save(card);
    }

    @Override
    public CardResponse findCardById(Long cardId) {
        log.debug("Find Card");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found" + username));
        Card card = getBankCard(cardId);
        log.debug("Card found");
        return cardMapper.toDto(card);
    }

    @Override
    public CardResponse blockCard(Long cardId) {
        log.debug("Block Card");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Card card = getBankCard(cardId);
        checkUsername(card, username);
        if (card.getCardStatus() == CardStatus.ACTIVE) {
            log.error("Card is blocked");
            throw new IllegalStateException("Active card can be blocked");
        }
        card.setCardStatus(CardStatus.BLOCKED);
        card = cardRepository.save(card);

        log.debug("Card blocked");
        return cardMapper.toDto(card);
    }

    @Override
    public BalanceResponse getBalance(Long cardId) {
        log.debug("Get Balance");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Card card = getBankCard(cardId);
        checkUsername(card, username);
        log.debug("Card found");
        return new BalanceResponse(cardId, card.getBalance());
    }

    @Override
    public Page<CardResponse> getAllCards(CardFilter cardFilter) {
        log.debug("Get All Cards");
        Page<Card> page = cardRepository.findAll(PageRequest.of(cardFilter.getOffset(), cardFilter.getLimit()));
        return page.map(cardMapper::toDto);
    }

    private Card getBankCard(Long cardId) {
        log.debug("Get Bank Card");
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Not found card by id = " + cardId));
    }

    private void checkUsername(Card card, String username) {
        if (!card.getOwner().getUsername().equals(username)) {
            log.error("Card ownership validation failed: cardOwner={}, requester={}",
                    card.getOwner().getUsername(), username);
            throw new CardNotFoundException("Access denied. Card belongs to another user");
        }
    }

    private void checkRoleAdmin(User user) {
        if (!user.getRole().equals(Role.ADMIN)) {
            log.error("User role validation failed: user={}", user.getUsername());
            throw new UserNotFoundException("Access denied. Insufficient rights");
        }
    }
}