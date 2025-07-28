package com.example.bankcards.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CardType {
    VISA(1, "Visa", "411111", "4[0-9]{12,15}", 16),
    MASTERCARD(2, "MasterCard", "511111", "5[1-5][0-9]{14}", 16);

    public static final String MASKED_CARD_PATTERN = "^(\\*{4} ?)*\\d{4}$";
    public static final int DIGIT_IN_ONE_SECTION = 4;

    private final int typeCode;
    private final String cardName;
    private final String prefix;
    private final String regex;
    private final int length;
}
