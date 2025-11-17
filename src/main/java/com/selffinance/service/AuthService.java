package com.selffinance.service;

import com.selffinance.domain.User;
import com.selffinance.repository.UserRepository;

import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;
    private User currentUser;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        if (PasswordEncoder.matches(password, user.getPasswordHash())) {
            this.currentUser = user;
            return true;
        }

        return false;
    }

    public boolean register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            return false;
        }

        String passwordHash = PasswordEncoder.encode(password);
        User newUser = new User(username, passwordHash);
        userRepository.save(newUser);
        this.currentUser = newUser;
        return true;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }
}
