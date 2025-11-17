package com.selffinance.service;

import java.nio.charset.StandardCharsets;

public class PasswordEncoder {
    public static String encode(String password) {
        StringBuilder hex = new StringBuilder();
        for (byte b : password.getBytes(StandardCharsets.UTF_8)) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

    public static boolean matches(String password, String encodedPassword) {
        return encode(password).equals(encodedPassword);
    }
}
