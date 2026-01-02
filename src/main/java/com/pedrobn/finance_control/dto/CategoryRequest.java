package com.pedrobn.finance_control.dto;

import java.math.BigDecimal;

import com.pedrobn.finance_control.model.TransactionType;

public record CategoryRequest(String name, TransactionType type, BigDecimal budgetLimit) {
}
