package com.selffinance.domain;

public enum OperationType {
    INCOME("+"),
    EXPENSE("-"),
    BUDGET("!");

    private final String prefix;

    OperationType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public static OperationType fromPrefix(String prefix) {
        for (OperationType type : values()) {
            if (type.prefix.equals(prefix)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown prefix: " + prefix);
    }
}
