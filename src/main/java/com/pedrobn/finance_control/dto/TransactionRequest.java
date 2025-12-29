package com.pedrobn.finance_control.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionRequest(
    String description,
    BigDecimal amount,
    LocalDateTime date
) {
}
