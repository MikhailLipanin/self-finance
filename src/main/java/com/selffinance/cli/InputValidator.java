package com.selffinance.cli;

public class InputValidator {
    public static ValidationResult validateOperationInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return ValidationResult.empty();
        }

        String trimmed = input.trim();
        int colonIndex = trimmed.lastIndexOf(':');

        if (colonIndex == -1 || colonIndex == 0 || colonIndex == trimmed.length() - 1) {
            return ValidationResult.error("Неверный формат. Ожидается: 'категория:сумма'");
        }

        String category = trimmed.substring(0, colonIndex).trim();
        String amountStr = trimmed.substring(colonIndex + 1).trim();

        if (category.isEmpty()) {
            return ValidationResult.error("Категория не может быть пустой");
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount < 0) {
                return ValidationResult.error("Сумма не может быть отрицательной");
            }
            return ValidationResult.success(category, amount);
        } catch (NumberFormatException e) {
            return ValidationResult.error("Сумма должна быть числом");
        }
    }

    public static class ValidationResult {
        private final boolean valid;
        private final boolean empty;
        private final String errorMessage;
        private final String category;
        private final double amount;

        private ValidationResult(boolean valid, boolean empty, String errorMessage, String category, double amount) {
            this.valid = valid;
            this.empty = empty;
            this.errorMessage = errorMessage;
            this.category = category;
            this.amount = amount;
        }

        public static ValidationResult success(String category, double amount) {
            return new ValidationResult(true, false, null, category, amount);
        }

        public static ValidationResult error(String errorMessage) {
            return new ValidationResult(false, false, errorMessage, null, 0);
        }

        public static ValidationResult empty() {
            return new ValidationResult(false, true, null, null, 0);
        }

        public boolean isValid() {
            return valid;
        }

        public boolean isEmpty() {
            return empty;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public String getCategory() {
            return category;
        }

        public double getAmount() {
            return amount;
        }
    }
}
