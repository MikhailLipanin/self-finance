package com.selffinance.domain;

import java.util.*;

public class Wallet {
    private final List<Operation> operations;
    private final Map<String, Double> categoryBudgets;

    public Wallet() {
        this.operations = new ArrayList<>();
        this.categoryBudgets = new HashMap<>();
    }

    public Wallet(List<Operation> operations, Map<String, Double> categoryBudgets) {
        this.operations = new ArrayList<>(operations);
        this.categoryBudgets = new HashMap<>(categoryBudgets);
    }

    public void addOperation(Operation operation) {
        operations.add(operation);
        if (operation.getType() == OperationType.BUDGET) {
            categoryBudgets.put(operation.getCategory(), operation.getAmount());
        }
    }

    public List<Operation> getOperations() {
        return Collections.unmodifiableList(operations);
    }

    public Map<String, Double> getCategoryBudgets() {
        return Collections.unmodifiableMap(categoryBudgets);
    }

    public double getTotalIncome() {
        return operations.stream()
                .filter(op -> op.getType() == OperationType.INCOME)
                .mapToDouble(Operation::getAmount)
                .sum();
    }

    public double getTotalExpenses() {
        return operations.stream()
                .filter(op -> op.getType() == OperationType.EXPENSE)
                .mapToDouble(Operation::getAmount)
                .sum();
    }

    public double getIncomeByCategory(String category) {
        return operations.stream()
                .filter(op -> op.getType() == OperationType.INCOME && op.getCategory().equals(category))
                .mapToDouble(Operation::getAmount)
                .sum();
    }

    public double getExpensesByCategory(String category) {
        return operations.stream()
                .filter(op -> op.getType() == OperationType.EXPENSE && op.getCategory().equals(category))
                .mapToDouble(Operation::getAmount)
                .sum();
    }

    public Set<String> getAllCategories() {
        Set<String> categories = new HashSet<>();
        for (Operation op : operations) {
            if (op.getType() != OperationType.BUDGET) {
                categories.add(op.getCategory());
            }
        }
        return categories;
    }

    public Set<String> getBudgetCategories() {
        return new HashSet<>(categoryBudgets.keySet());
    }

    public double getBudgetForCategory(String category) {
        return categoryBudgets.getOrDefault(category, 0.0);
    }

    public double getRemainingBudget(String category) {
        double budget = getBudgetForCategory(category);
        double expenses = getExpensesByCategory(category);
        return budget - expenses;
    }
}
