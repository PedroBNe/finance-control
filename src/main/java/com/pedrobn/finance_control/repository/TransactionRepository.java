package com.pedrobn.finance_control.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pedrobn.finance_control.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}