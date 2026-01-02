package com.pedrobn.finance_control.dto;

import java.math.BigDecimal;

public record CategoryBudgetResponse(
    Long id,
    String name,
    BigDecimal budgetLimit,
    BigDecimal currentSpent,
    BigDecimal percentage
) {}