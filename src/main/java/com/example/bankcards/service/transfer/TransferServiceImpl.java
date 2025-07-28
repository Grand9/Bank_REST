package com.example.bankcards.service.transfer;

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.TransferStatus;
import com.example.bankcards.exception.TransferException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.util.mapper.TransferMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferServiceImpl implements TransferService {
    private final TransferRepository transferRepository;
    private final CardRepository cardRepository;
    private final TransferMapper transferMapper;

    @Override
    public TransferResponse transfer(TransferResponse transferResponse) {
        log.info("Transfer request received");
        Card toCard = cardRepository.findById(transferResponse.getToCardId()).orElseThrow();
        Long toCardId = transferResponse.getToCardId();

        log.debug("Transfer to card id: {}", toCardId);
        Card fromCard = cardRepository.findById(transferResponse.getFromCardId()).orElseThrow();
        Long fromCardId = transferResponse.getFromCardId();

        log.debug("Transfer from card id: {}", fromCardId);
        checkIfSenderCardIsLocked(fromCard.getCardStatus(), fromCard.getId());
        checkIfTransferAvailableForYourselfOnly(fromCardId,toCardId, fromCard.getId(), toCard.getId());

        BigDecimal amount = transferResponse.getAmount();
        BigDecimal toCardBalance = toCard.getBalance().add(amount);

        toCard.setBalance(toCardBalance);
        fromCard.setBalance(fromCard.getBalance().subtract(amount));

        log.debug("Transfer to card balance: {}", toCardBalance);
        cardRepository.saveAll(List.of(fromCard, toCard));

        transferResponse.setStatus(TransferStatus.COMPLETED);
        transferResponse.setCreatedAt(LocalDateTime.now());

        Transfer transfer = transferRepository.findById(transferResponse.getId()).orElseThrow();
        transfer.setStatus(TransferStatus.COMPLETED);
        transfer.setFromCard(fromCard);
        transfer.setToCard(toCard);
        transfer.setCreatedAt(LocalDateTime.now());
        log.debug("Transfer completed");
        return transferMapper.toDto(transferRepository.save(transfer));
    }

    @Override
    public void cancelTransfer(TransferResponse transferResponse) {
        log.debug("Transfer request received");
        Transfer transfer = transferMapper.toEntity(transferResponse);
        Card card = cardRepository.findById(transferResponse.getFromCardId()).orElseThrow();

        transfer.setFromCard(card);

        BigDecimal transferAmount = transfer.getAmount();

        Card fromCard = transfer.getFromCard();

        fromCard.setBalance(fromCard.getBalance().add(transferAmount));
        cardRepository.save(fromCard);

        transfer.setStatus(TransferStatus.FAILED);

        transferRepository.save(transfer);
        }


    private void checkIfTransferAvailableForYourselfOnly(Long fromCardOwnerId, Long toCardOwnerId,
                                                         Long fromCardId, Long toCardId) {
        if (!fromCardOwnerId.equals(toCardOwnerId)) {
            log.error("Transfers are allowed only between your own cards. From card id: {}, to card id: {}", fromCardId, toCardId);
            throw new TransferException("Transfers are allowed only between your own cards.!. From card id: " + fromCardId + ", to card id: " + toCardId);
        }
    }

    private void checkIfSenderCardIsLocked(CardStatus status, Long cardId) {
        if (status == CardStatus.EXPIRED || status == CardStatus.BLOCKED) {
            log.error("The sender's card has expired or is blocked. Card id: {}", cardId);
            throw new TransferException("The sender's card has expired or is blocked!. Card id: " + cardId);
        }
    }
}