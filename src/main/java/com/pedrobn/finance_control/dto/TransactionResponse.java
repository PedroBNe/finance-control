package com.pedrobn.finance_control.dto;

import java.math.BigDecimal;

public record TransactionResponse(
    Long id,
    String description,
    BigDecimal amount,
    String date,
    Long user
) {
}
