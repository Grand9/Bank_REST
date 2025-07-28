package com.example.bankcards.dto.request;

import com.example.bankcards.entity.enums.CardType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardTypeRequest {

    @NotNull
    private Long id;

    @NotNull
    private CardType cardType;
}