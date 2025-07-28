package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Card;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-28T17:03:03+0500",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.7 (Ubuntu)"
)
@Component
public class CardMapperImpl implements CardMapper {

    @Override
    public Card toEntity(CardResponse cardResponse) {
        if ( cardResponse == null ) {
            return null;
        }

        Card.CardBuilder card = Card.builder();

        card.id( cardResponse.getId() );
        card.cardNumber( cardResponse.getCardNumber() );
        card.cardStatus( cardResponse.getCardStatus() );
        card.expirationDate( cardResponse.getExpirationDate() );
        card.balance( cardResponse.getBalance() );

        return card.build();
    }

    @Override
    public CardResponse toDto(Card card) {
        if ( card == null ) {
            return null;
        }

        CardResponse.CardResponseBuilder cardResponse = CardResponse.builder();

        cardResponse.id( card.getId() );
        cardResponse.cardNumber( card.getCardNumber() );
        cardResponse.expirationDate( card.getExpirationDate() );
        cardResponse.cardStatus( card.getCardStatus() );
        cardResponse.balance( card.getBalance() );

        return cardResponse.build();
    }
}
