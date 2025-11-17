package com.selffinance.service;

import com.selffinance.domain.Operation;
import com.selffinance.domain.OperationType;
import com.selffinance.domain.User;
import com.selffinance.domain.Wallet;

import java.util.*;

public class FinanceService {
    private final User user;

    public FinanceService(User user) {
        this.user = user;
    }

    public void addIncome(String category, double amount) {
        Operation operation = new Operation(OperationType.INCOME, category, amount);
        user.getWallet().addOperation(operation);
    }

    public void addExpense(String category, double amount) {
        Operation operation = new Operation(OperationType.EXPENSE, category, amount);
        user.getWallet().addOperation(operation);
    }

    public void setBudget(String category, double amount) {
        Operation operation = new Operation(OperationType.BUDGET, category, amount);
        user.getWallet().addOperation(operation);
    }

    public Wallet getWallet() {
        return user.getWallet();
    }

    public List<String> checkBudgetExceeded() {
        List<String> warnings = new ArrayList<>();
        Wallet wallet = user.getWallet();

        for (String category : wallet.getBudgetCategories()) {
            double remaining = wallet.getRemainingBudget(category);
            if (remaining < 0) {
                warnings.add(String.format("Внимание! Превышен лимит бюджета по категории '%s' на %.2f",
                        category, Math.abs(remaining)));
            }
        }

        return warnings;
    }

    public Optional<String> checkExpensesExceedIncome() {
        Wallet wallet = user.getWallet();
        double totalIncome = wallet.getTotalIncome();
        double totalExpenses = wallet.getTotalExpenses();

        if (totalExpenses > totalIncome) {
            return Optional.of(String.format("Внимание! Расходы (%.2f) превышают доходы (%.2f) на %.2f",
                    totalExpenses, totalIncome, totalExpenses - totalIncome));
        }

        return Optional.empty();
    }

    public Map<String, Double> getIncomeByCategories() {
        Map<String, Double> incomeByCategory = new HashMap<>();
        Wallet wallet = user.getWallet();

        for (String category : wallet.getAllCategories()) {
            double income = wallet.getIncomeByCategory(category);
            if (income > 0) {
                incomeByCategory.put(category, income);
            }
        }

        return incomeByCategory;
    }

    public Map<String, Double> getExpensesByCategories() {
        Map<String, Double> expensesByCategory = new HashMap<>();
        Wallet wallet = user.getWallet();

        for (String category : wallet.getAllCategories()) {
            double expenses = wallet.getExpensesByCategory(category);
            if (expenses > 0) {
                expensesByCategory.put(category, expenses);
            }
        }

        return expensesByCategory;
    }

    public Map<String, Double> calculateByCategories(Set<String> categories) {
        Map<String, Double> result = new HashMap<>();
        Wallet wallet = user.getWallet();

        for (String category : categories) {
            double income = wallet.getIncomeByCategory(category);
            double expenses = wallet.getExpensesByCategory(category);
            result.put(category, income - expenses);
        }

        return result;
    }
}
