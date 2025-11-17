package com.selffinance.domain;

import java.util.Objects;

public class Operation {
    private final OperationType type;
    private final String category;
    private final double amount;

    public Operation(OperationType type, String category, double amount) {
        this.type = type;
        this.category = category;
        this.amount = amount;
    }

    public OperationType getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Operation operation = (Operation) o;
        return Double.compare(operation.amount, amount) == 0 &&
                type == operation.type &&
                Objects.equals(category, operation.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, category, amount);
    }

    @Override
    public String toString() {
        return type.getPrefix() + category + ":" + amount;
    }
}
