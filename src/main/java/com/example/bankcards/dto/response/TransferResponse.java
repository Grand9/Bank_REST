package com.example.bankcards.dto.response;

import com.example.bankcards.entity.enums.TransferStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferResponse {

    @NotNull
    private Long id;

    @NotNull
    private Long fromCardId;

    @NotNull
    private Long toCardId;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal amount;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @NotNull
    private TransferStatus status;
}
