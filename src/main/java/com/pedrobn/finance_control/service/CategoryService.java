package com.pedrobn.finance_control.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pedrobn.finance_control.dto.CategoryBudgetResponse;
import com.pedrobn.finance_control.dto.CategoryRequest;
import com.pedrobn.finance_control.dto.CategoryResponse;
import com.pedrobn.finance_control.model.Category;
import com.pedrobn.finance_control.model.Transaction;
import com.pedrobn.finance_control.model.TransactionType;
import com.pedrobn.finance_control.model.User;
import com.pedrobn.finance_control.repository.CategoryRepository;
import com.pedrobn.finance_control.repository.TransactionRepository;
import com.pedrobn.finance_control.security.AuthContext;

@Service
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final AuthContext authContext;

    public CategoryService(CategoryRepository categoryRepository, TransactionRepository transactionRepository, AuthContext authContext) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.authContext = authContext;
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        User user = authContext.getLoggedUser();
        
        Category category = new Category();
        category.setName(request.name());
        category.setType(request.type());
        category.setBudgetLimit(request.budgetLimit());
        category.setUser(user);
        
        Category savedCategory = categoryRepository.save(category);
        
        return new CategoryResponse(
            savedCategory.getId(),
            savedCategory.getName(),
            savedCategory.getType(),
            savedCategory.getBudgetLimit()
        );
    }

    public List<CategoryResponse> getAllCategories() {
        User user = authContext.getLoggedUser();
        return categoryRepository.findByUserId(user.getId()).stream()
            .map(category -> new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType(),
                category.getBudgetLimit()
            ))
            .collect(Collectors.toList());
    }
    
    public Category getCategoryById(Long id) {
        if (id == null) return null;
        return categoryRepository.findById(id).orElse(null);
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        User user = authContext.getLoggedUser();
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        category.setName(request.name());
        category.setType(request.type());
        category.setBudgetLimit(request.budgetLimit());

        Category savedCategory = categoryRepository.save(category);

        return new CategoryResponse(
            savedCategory.getId(),
            savedCategory.getName(),
            savedCategory.getType(),
            savedCategory.getBudgetLimit()
        );
    }

    public void deleteCategory(Long id) {
        User user = authContext.getLoggedUser();
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        categoryRepository.delete(category);
    }

    public List<CategoryBudgetResponse> getCategoryBudgets(String month) {
        User user = authContext.getLoggedUser();
        
        // Parse month (YYYY-MM)
        YearMonth yearMonth = YearMonth.parse(month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        // Get all categories for user (filtered by EXPENSE, usually budgets apply to expenses)
        List<Category> categories = categoryRepository.findByUserId(user.getId()).stream()
            .filter(c -> c.getType() == TransactionType.EXPENSE)
            .collect(Collectors.toList());

        // Get transactions for the month
        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(user.getId(), start, end);

        // Group transactions by category ID and sum amount
        Map<Long, BigDecimal> spentByCategory = transactions.stream()
            .filter(t -> t.getCategory() != null && t.getType() == TransactionType.EXPENSE)
            .collect(Collectors.groupingBy(
                t -> t.getCategory().getId(),
                Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
            ));

        return categories.stream()
            .map(category -> {
                BigDecimal limit = category.getBudgetLimit();
                BigDecimal spent = spentByCategory.getOrDefault(category.getId(), BigDecimal.ZERO);
                BigDecimal percentage = BigDecimal.ZERO;

                if (limit != null && limit.compareTo(BigDecimal.ZERO) > 0) {
                    percentage = spent.divide(limit, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                }

                return new CategoryBudgetResponse(
                    category.getId(),
                    category.getName(),
                    limit,
                    spent,
                    percentage
                );
            })
            .collect(Collectors.toList());
    }
}
