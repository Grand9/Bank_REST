package com.example.bankcards.service;

import com.example.bankcards.dto.response.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.entity.enums.TransferStatus;
import com.example.bankcards.exception.TransferException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.service.transfer.TransferServiceImpl;
import com.example.bankcards.util.mapper.TransferMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    @Mock
    private TransferRepository transferRepository;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private TransferMapper transferMapper;

    @InjectMocks
    private TransferServiceImpl transferService;

    private Card fromCard;
    private Card toCard;
    private TransferResponse transferResponse;
    private Transfer transfer;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("tester")
                .role(Role.USER)
                .build();
        fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setOwner(user);
        fromCard.setCardStatus(CardStatus.ACTIVE);
        fromCard.setBalance(BigDecimal.valueOf(1000));

        toCard = new Card();
        toCard.setId(2L);
        toCard.setOwner(user);
        toCard.setCardStatus(CardStatus.ACTIVE);
        toCard.setBalance(BigDecimal.valueOf(500));

        transferResponse = new TransferResponse();
        transferResponse.setId(99L);
        transferResponse.setFromCardId(fromCard.getId());
        transferResponse.setToCardId(toCard.getId());
        transferResponse.setAmount(BigDecimal.valueOf(200));

        transfer = new Transfer();
        transfer.setId(99L);
        transfer.setAmount(BigDecimal.valueOf(200));
    }

    @Test
    void transfer_ShouldThrow_WhenFromCardIsBlocked() {
        fromCard.setCardStatus(CardStatus.BLOCKED);
        given(cardRepository.findById(fromCard.getId())).willReturn(Optional.of(fromCard));
        given(cardRepository.findById(toCard.getId())).willReturn(Optional.of(toCard));

        assertThatThrownBy(() -> transferService.transfer(transferResponse))
                .isInstanceOf(TransferException.class)
                .hasMessageContaining("expired or is blocked");
    }

    @Test
    void transfer_ShouldThrow_WhenCardsBelongToDifferentOwners() {
        toCard.setOwner(user);
        given(cardRepository.findById(fromCard.getId())).willReturn(Optional.of(fromCard));
        given(cardRepository.findById(toCard.getId())).willReturn(Optional.of(toCard));

        assertThatThrownBy(() -> transferService.transfer(transferResponse))
                .isInstanceOf(TransferException.class)
                .hasMessageContaining("only between your own cards");
    }

    @Test
    void cancelTransfer_ShouldSucceed() {
        // given
        transfer.setFromCard(fromCard);
        transfer.setStatus(TransferStatus.PENDING);
        given(transferMapper.toEntity(transferResponse)).willReturn(transfer);
        given(cardRepository.findById(fromCard.getId())).willReturn(Optional.of(fromCard));
        given(transferRepository.save(any())).willReturn(transfer);

        // when
        transferService.cancelTransfer(transferResponse);

        // then
        assertThat(fromCard.getBalance()).isEqualByComparingTo("1200");
        verify(cardRepository).save(fromCard);
        verify(transferRepository).save(transfer);
        assertThat(transfer.getStatus()).isEqualTo(TransferStatus.FAILED);
    }
}