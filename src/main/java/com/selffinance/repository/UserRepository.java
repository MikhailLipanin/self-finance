package com.selffinance.repository;

import com.selffinance.domain.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);

    void save(User user);

    void saveAll();

    void loadAll();
}
