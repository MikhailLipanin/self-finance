package com.selffinance.cli;

import com.selffinance.domain.Wallet;

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class OutputFormatter {
    private final PrintStream output;

    public OutputFormatter(PrintStream output) {
        this.output = output;
    }

    public void printWalletSummary(Wallet wallet) {
        output.println("Общий доход: " + formatAmount(wallet.getTotalIncome()));
        output.println();

        Map<String, Double> incomeByCategory = getIncomeByCategories(wallet);
        if (!incomeByCategory.isEmpty()) {
            output.println("Доходы по категориям:");
            for (Map.Entry<String, Double> entry : incomeByCategory.entrySet()) {
                output.println("  - " + entry.getKey() + ": " + formatAmount(entry.getValue()));
            }
            output.println();
        }

        output.println("Общие расходы: " + formatAmount(wallet.getTotalExpenses()));
        output.println();

        Map<String, Double> budgetInfo = getBudgetInfo(wallet);
        if (!budgetInfo.isEmpty()) {
            output.println("Бюджет по категориям:");
            for (String category : budgetInfo.keySet()) {
                double budget = wallet.getBudgetForCategory(category);
                double remaining = wallet.getRemainingBudget(category);
                output.println(String.format("  - %s: %s, Оставшийся бюджет: %s",
                        category, formatAmount(budget), formatAmount(remaining)));
            }
        }
    }

    private Map<String, Double> getIncomeByCategories(Wallet wallet) {
        Map<String, Double> incomeByCategory = new java.util.HashMap<>();
        for (String category : wallet.getAllCategories()) {
            double income = wallet.getIncomeByCategory(category);
            if (income > 0) {
                incomeByCategory.put(category, income);
            }
        }
        return incomeByCategory;
    }

    private Map<String, Double> getBudgetInfo(Wallet wallet) {
        // Sort by remaining budget: negative first, then by category name
        return wallet.getBudgetCategories().stream()
                .sorted((c1, c2) -> {
                    double rem1 = wallet.getRemainingBudget(c1);
                    double rem2 = wallet.getRemainingBudget(c2);
                    if (rem1 < 0 && rem2 >= 0)
                        return -1;
                    if (rem1 >= 0 && rem2 < 0)
                        return 1;
                    if (rem1 != rem2)
                        return Double.compare(rem1, rem2);
                    return c1.compareTo(c2);
                })
                .collect(Collectors.toMap(
                        category -> category,
                        wallet::getBudgetForCategory,
                        (a, b) -> a,
                        LinkedHashMap::new));
    }

    private String formatAmount(double amount) {
        return String.format("%.1f", amount);
    }
}
