package com.pedrobn.finance_control.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pedrobn.finance_control.dto.TransactionRequest;
import com.pedrobn.finance_control.dto.TransactionResponse;
import com.pedrobn.finance_control.model.Transaction;
import com.pedrobn.finance_control.model.User;
import com.pedrobn.finance_control.repository.TransactionRepository;
import com.pedrobn.finance_control.security.AuthContext;
import com.pedrobn.finance_control.util.FormattedDate;

@Service
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final AuthContext authContext;

    public TransactionService(TransactionRepository transactionRepository, AuthContext authContext) {
        this.transactionRepository = transactionRepository;
        this.authContext = authContext;
    }

    public TransactionResponse createTransaction(TransactionRequest request) {
        User user = authContext.getLoggedUser();
        
        Transaction transaction = new Transaction();
        transaction.setDescription(request.description());
        transaction.setAmount(request.amount());
        transaction.setTransactionDate(request.date());
        transaction.setUser(user);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        return new TransactionResponse(
            savedTransaction.getId(),
            savedTransaction.getDescription(),
            savedTransaction.getAmount(),
            FormattedDate.format(savedTransaction.getTransactionDate()),
            savedTransaction.getUser().getId()
        );
    }

    public List<TransactionResponse> getAllTransactions() {
        User user = authContext.getLoggedUser();
        return transactionRepository.findByUserId(user.getId()).stream()
            .map(transaction -> new TransactionResponse(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                FormattedDate.format(transaction.getTransactionDate()),
                transaction.getUser().getId()
            ))
            .collect(Collectors.toList());
    }
}
