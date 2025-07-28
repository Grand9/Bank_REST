package com.example.bankcards.dto.response;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceResponse {

    @NotNull
    private Long cardId;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal balance;
}
