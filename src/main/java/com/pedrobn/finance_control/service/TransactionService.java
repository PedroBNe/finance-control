package com.pedrobn.finance_control.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pedrobn.finance_control.dto.TransactionRequest;
import com.pedrobn.finance_control.dto.TransactionResponse;
import com.pedrobn.finance_control.model.Category;
import com.pedrobn.finance_control.model.Transaction;
import com.pedrobn.finance_control.model.User;
import com.pedrobn.finance_control.repository.TransactionRepository;
import com.pedrobn.finance_control.security.AuthContext;
import com.pedrobn.finance_control.util.FormattedDate;

@Service
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;
    private final AuthContext authContext;

    public TransactionService(TransactionRepository transactionRepository, CategoryService categoryService, AuthContext authContext) {
        this.transactionRepository = transactionRepository;
        this.categoryService = categoryService;
        this.authContext = authContext;
    }

    public TransactionResponse createTransaction(TransactionRequest request) {
        User user = authContext.getLoggedUser();
        
        Transaction transaction = new Transaction();
        transaction.setDescription(request.description());
        transaction.setAmount(request.amount());
        transaction.setTransactionDate(request.date());
        transaction.setType(request.type());
        transaction.setUser(user);

        if (request.categoryId() != null) {
            Category category = categoryService.getCategoryById(request.categoryId());
            if (category != null) {
                transaction.setCategory(category);
            }
        }
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        return new TransactionResponse(
            savedTransaction.getId(),
            savedTransaction.getDescription(),
            savedTransaction.getAmount(),
            FormattedDate.format(savedTransaction.getTransactionDate()),
            savedTransaction.getUser().getId(),
            savedTransaction.getType(),
            savedTransaction.getCategory() != null ? savedTransaction.getCategory().getName() : null
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
                transaction.getUser().getId(),
                transaction.getType(),
                transaction.getCategory() != null ? transaction.getCategory().getName() : null
            ))
            .collect(Collectors.toList());
    }

    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {
        User user = authContext.getLoggedUser();
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
            
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        transaction.setDescription(request.description());
        transaction.setAmount(request.amount());
        transaction.setTransactionDate(request.date());
        transaction.setType(request.type());

        if (request.categoryId() != null) {
            Category category = categoryService.getCategoryById(request.categoryId());
            if (category != null) {
                transaction.setCategory(category);
            }
        } else {
            transaction.setCategory(null);
        }

        Transaction savedTransaction = transactionRepository.save(transaction);

        return new TransactionResponse(
            savedTransaction.getId(),
            savedTransaction.getDescription(),
            savedTransaction.getAmount(),
            FormattedDate.format(savedTransaction.getTransactionDate()),
            savedTransaction.getUser().getId(),
            savedTransaction.getType(),
            savedTransaction.getCategory() != null ? savedTransaction.getCategory().getName() : null
        );
    }

    public void deleteTransaction(Long id) {
        User user = authContext.getLoggedUser();
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
            
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        transactionRepository.delete(transaction);
    }
}
