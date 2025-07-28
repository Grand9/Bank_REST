package com.example.bankcards.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CardFilter {
    private Integer offset;
    private Integer limit;
}