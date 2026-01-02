package com.pedrobn.finance_control.dto;

import java.math.BigDecimal;

import com.pedrobn.finance_control.model.TransactionType;

public record TransactionResponse(
    Long id,
    String description,
    BigDecimal amount,
    String date,
    Long user,
    TransactionType type,
    String categoryName
) {
}
