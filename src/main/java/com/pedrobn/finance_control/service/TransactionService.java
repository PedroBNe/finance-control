package com.pedrobn.finance_control.service;

import org.springframework.stereotype.Service;

import com.pedrobn.finance_control.dto.TransactionRequest;
import com.pedrobn.finance_control.dto.TransactionResponse;
import com.pedrobn.finance_control.model.Transaction;
import com.pedrobn.finance_control.repository.TransactionRepository;
import com.pedrobn.finance_control.util.FormattedDate;

@Service
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final UserService userService;

    public TransactionService(TransactionRepository transactionRepository, UserService userService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
    }

    TransactionResponse createTransaction(TransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setDescription(request.description());
        transaction.setAmount(request.amount());
        transaction.setTransactionDate(request.date());
        transaction.setUser(userService.getUserById(request.user()));
        Transaction savedTransaction = transactionRepository.save(transaction);
        return new TransactionResponse(
            savedTransaction.getId(),
            savedTransaction.getDescription(),
            savedTransaction.getAmount(),
            FormattedDate.format(savedTransaction.getTransactionDate()),
            savedTransaction.getUser().getId()
        );
    }
}
