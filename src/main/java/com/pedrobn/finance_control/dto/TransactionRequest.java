package com.pedrobn.finance_control.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.pedrobn.finance_control.model.TransactionType;

public record TransactionRequest(
    String description,
    BigDecimal amount,
    LocalDateTime date,
    TransactionType type,
    Long categoryId
) {
}
