package com.example.bankcards.dto.request;

import com.example.bankcards.dto.EncryptedCardNumber;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferRequest {

    @NotNull
    private Long fromCardId;

    @NotNull
    private Long toCardId;

    @NotNull
    private EncryptedCardNumber fromCardNumber;

    @NotNull
    private EncryptedCardNumber toCardNumber;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal amount;
}