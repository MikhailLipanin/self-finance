package com.selffinance;

import com.selffinance.cli.CLI;
import com.selffinance.repository.JsonUserRepository;
import com.selffinance.repository.UserRepository;
import com.selffinance.service.AuthService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        UserRepository userRepository = new JsonUserRepository();
        AuthService authService = new AuthService(userRepository);
        Scanner scanner = new Scanner(System.in);

        CLI cli = new CLI(scanner, authService, userRepository);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            userRepository.saveAll();
        }));

        cli.run();
    }
}
