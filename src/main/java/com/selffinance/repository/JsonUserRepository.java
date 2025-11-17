package com.selffinance.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.selffinance.domain.Operation;
import com.selffinance.domain.OperationType;
import com.selffinance.domain.User;
import com.selffinance.domain.Wallet;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class JsonUserRepository implements UserRepository {
    private static final String DATA_FILE = "data.json";
    private final Map<String, User> users;
    private final Gson gson;

    public JsonUserRepository() {
        this.users = new HashMap<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadAll();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    @Override
    public void save(User user) {
        users.put(user.getUsername(), user);
    }

    @Override
    public void saveAll() {
        try {
            JsonObject root = new JsonObject();
            for (User user : users.values()) {
                JsonObject userJson = new JsonObject();
                userJson.addProperty("password", user.getPasswordHash());

                JsonObject opHistory = new JsonObject();
                Wallet wallet = user.getWallet();
                for (Operation op : wallet.getOperations()) {
                    String key = op.getType().getPrefix() + op.getCategory();
                    opHistory.addProperty(key, op.getAmount());
                }

                userJson.add("opHistory", opHistory);
                root.add(user.getUsername(), userJson);
            }

            try (FileWriter writer = new FileWriter(DATA_FILE)) {
                gson.toJson(root, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save data to file", e);
        }
    }

    @Override
    public void loadAll() {
        if (!Files.exists(Paths.get(DATA_FILE))) {
            return;
        }

        try (Reader reader = new FileReader(DATA_FILE)) {
            JsonObject root = gson.fromJson(reader, JsonObject.class);
            if (root == null) {
                return;
            }

            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                String username = entry.getKey();
                JsonObject userJson = entry.getValue().getAsJsonObject();
                String passwordHash = userJson.get("password").getAsString();

                Wallet wallet = new Wallet();
                if (userJson.has("opHistory")) {
                    JsonObject opHistory = userJson.getAsJsonObject("opHistory");
                    for (Map.Entry<String, JsonElement> opEntry : opHistory.entrySet()) {
                        String key = opEntry.getKey();
                        double amount = opEntry.getValue().getAsDouble();

                        String prefix = key.substring(0, 1);
                        String category = key.substring(1);
                        OperationType type = OperationType.fromPrefix(prefix);

                        wallet.addOperation(new Operation(type, category, amount));
                    }
                }

                User user = new User(username, passwordHash, wallet);
                users.put(username, user);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load data from file", e);
        }
    }
}
