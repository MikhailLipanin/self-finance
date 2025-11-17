package com.selffinance.cli;

import com.selffinance.repository.UserRepository;
import com.selffinance.service.AuthService;
import com.selffinance.service.FinanceService;

import java.io.*;
import java.util.*;

public class CLI {
    private final Scanner scanner;
    private final AuthService authService;
    private final UserRepository userRepository;
    private FinanceService financeService;
    private OutputFormatter outputFormatter;
    private PrintStream currentOutput;

    public CLI(Scanner scanner, AuthService authService, UserRepository userRepository) {
        this.scanner = scanner;
        this.authService = authService;
        this.userRepository = userRepository;
        this.currentOutput = System.out;
        this.outputFormatter = new OutputFormatter(currentOutput);
    }

    public void run() {
        printWelcome();

        while (true) {
            if (!authService.isAuthenticated()) {
                if (!handleAuthentication()) {
                    continue;
                }
                financeService = new FinanceService(authService.getCurrentUser());
            }

            printMainMenu();
            String command = scanner.nextLine().trim();

            switch (command.toLowerCase()) {
                case "1":
                    handleAddIncome();
                    break;
                case "2":
                    handleAddExpense();
                    break;
                case "3":
                    handleSetBudget();
                    break;
                case "4":
                    handleShowSummary();
                    break;
                case "5":
                    handleSaveToFile();
                    break;
                case "6":
                    handleLogout();
                    break;
                case "7":
                    handleExit();
                    return;
                default:
                    System.out.println("Неизвестная команда. Попробуйте снова.");
            }
        }
    }

    private void printWelcome() {
        System.out.println("Система управления личными финансами");
        System.out.println();
    }

    private boolean handleAuthentication() {
        System.out.println("1. Вход");
        System.out.println("2. Регистрация");
        System.out.print("Выберите действие: ");
        String choice = scanner.nextLine().trim();

        System.out.print("Введите имя пользователя: ");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Имя пользователя не может быть пустым.");
            return false;
        }

        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        if (choice.equals("1")) {
            if (authService.login(username, password)) {
                System.out.println("Успешный вход!");
                return true;
            } else {
                System.out.println("Неверное имя пользователя или пароль.");
                return false;
            }
        } else if (choice.equals("2")) {
            if (authService.register(username, password)) {
                System.out.println("Регистрация успешна!");
                return true;
            } else {
                System.out.println("Пользователь с таким именем уже существует.");
                return false;
            }
        } else {
            System.out.println("Неверный выбор.");
            return false;
        }
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("Главное меню");
        System.out.println("Пользователь: " + authService.getCurrentUser().getUsername());
        System.out.println("1. Добавить доходы");
        System.out.println("2. Добавить расходы");
        System.out.println("3. Установить бюджет");
        System.out.println("4. Показать сводку");
        System.out.println("5. Сохранить в файл");
        System.out.println("6. Выйти из аккаунта");
        System.out.println("7. Выход из приложения");
        System.out.print("Выберите команду: ");
    }

    private void handleAddIncome() {
        System.out.println("Введите доходы в формате 'категория:сумма' (пустая строка для завершения):");
        List<String> warnings = new ArrayList<>();

        while (true) {
            String input = scanner.nextLine().trim();
            InputValidator.ValidationResult result = InputValidator.validateOperationInput(input);

            if (result.isEmpty()) {
                break;
            }

            if (!result.isValid()) {
                System.out.println("Ошибка: " + result.getErrorMessage());
                continue;
            }

            financeService.addIncome(result.getCategory(), result.getAmount());

            List<String> budgetWarnings = financeService.checkBudgetExceeded();
            warnings.addAll(budgetWarnings);

            Optional<String> incomeWarning = financeService.checkExpensesExceedIncome();
            incomeWarning.ifPresent(warnings::add);
        }

        if (!warnings.isEmpty()) {
            System.out.println();
            for (String warning : warnings) {
                System.out.println(warning);
            }
        }

        System.out.println("Доходы добавлены.");
    }

    private void handleAddExpense() {
        System.out.println("Введите расходы в формате 'категория:сумма' (пустая строка для завершения):");
        List<String> warnings = new ArrayList<>();

        while (true) {
            String input = scanner.nextLine().trim();
            InputValidator.ValidationResult result = InputValidator.validateOperationInput(input);

            if (result.isEmpty()) {
                break;
            }

            if (!result.isValid()) {
                System.out.println("Ошибка: " + result.getErrorMessage());
                continue;
            }

            financeService.addExpense(result.getCategory(), result.getAmount());

            List<String> budgetWarnings = financeService.checkBudgetExceeded();
            warnings.addAll(budgetWarnings);

            Optional<String> incomeWarning = financeService.checkExpensesExceedIncome();
            incomeWarning.ifPresent(warnings::add);
        }

        if (!warnings.isEmpty()) {
            System.out.println();
            for (String warning : warnings) {
                System.out.println(warning);
            }
        }

        System.out.println("Расходы добавлены.");
    }

    private void handleSetBudget() {
        System.out.println("Введите бюджеты в формате 'категория:сумма' (пустая строка для завершения):");

        while (true) {
            String input = scanner.nextLine().trim();
            InputValidator.ValidationResult result = InputValidator.validateOperationInput(input);

            if (result.isEmpty()) {
                break;
            }

            if (!result.isValid()) {
                System.out.println("Ошибка: " + result.getErrorMessage());
                continue;
            }

            financeService.setBudget(result.getCategory(), result.getAmount());
        }

        System.out.println("Бюджеты установлены.");
    }

    private void handleShowSummary() {
        outputFormatter.printWalletSummary(financeService.getWallet());
    }

    private void handleSaveToFile() {
        System.out.print("Введите имя файла для сохранения: ");
        String filename = scanner.nextLine().trim();

        if (filename.isEmpty()) {
            System.out.println("Имя файла не может быть пустым.");
            return;
        }

        try {
            PrintStream fileOutput = new PrintStream(new FileOutputStream(filename));
            OutputFormatter fileFormatter = new OutputFormatter(fileOutput);
            fileFormatter.printWalletSummary(financeService.getWallet());
            fileOutput.close();
            System.out.println("Данные сохранены в файл: " + filename);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении в файл: " + e.getMessage());
        }
    }

    private void handleLogout() {
        authService.logout();
        financeService = null;
        System.out.println("Вы вышли из аккаунта.");
    }

    private void handleExit() {
        userRepository.saveAll();
        System.out.println("Данные сохранены. До свидания!");
    }
}
