package com.example.bankcards.dto.response;

import com.example.bankcards.entity.enums.CardStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardResponse {

    private long id;

    private int cardTypeCode;

    @Pattern(regexp = "^(\\*{4} ?)*\\d{4}$")
    private String cardNumber;

    private Long ownerId;

    private LocalDateTime expirationDate;

    private boolean isDeleted;

    private CardStatus cardStatus;

    @DecimalMin(value = "0.00")
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;
}
