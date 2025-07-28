package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    @Query("SELECT c.id FROM Card c WHERE c.cardStatus = :cardStatus")
    Optional<Card> findByCardStatus(CardStatus cardStatus);

    @Query("SELECT c.id FROM Card c WHERE c.cardNumber = :encryptedCardNumber")
    Optional<Card> findIdByEncryptedCardNumber(String encryptedCardNumber);

    @Query("SELECT c.id FROM Card c WHERE c.expirationDate = :expirationDate")
    Optional<Card> findByCardExpirationDate(LocalDateTime expirationDate);

}