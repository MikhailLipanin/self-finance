package com.selffinance.domain;

import java.util.Objects;

public class User {
    private final String username;
    private final String passwordHash;
    private final Wallet wallet;

    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.wallet = new Wallet();
    }

    public User(String username, String passwordHash, Wallet wallet) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.wallet = wallet;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Wallet getWallet() {
        return wallet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
