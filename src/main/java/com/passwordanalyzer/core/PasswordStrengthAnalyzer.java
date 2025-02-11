package com.passwordanalyzer.core;

import java.util.ArrayList;
import java.util.List;

public class PasswordStrengthAnalyzer {
    private final PasswordDatabase passwordDB;

    public PasswordStrengthAnalyzer() {
        this.passwordDB = new PasswordDatabase();
        this.passwordDB.initializeDatabase();
    }

    public static class PasswordStrength {
        private final int score;
        private final List<String> suggestions;

        public PasswordStrength(int score, List<String> suggestions) {
            this.score = score;
            this.suggestions = suggestions;
        }

        public int getScore() {
            return score;
        }

        public List<String> getSuggestions() {
            return suggestions;
        }
    }

    public PasswordStrength analyzePassword(String password) {
        List<String> suggestions = new ArrayList<>();
        int score = 0;

        // Check if password is compromised
        if (passwordDB.isPasswordCompromised(password)) {
            suggestions.add("This password has been compromised! Please choose a different password.");
            return new PasswordStrength(0, suggestions);
        }

        // Basic length check
        if (password.length() < 8) {
            suggestions.add("Password should be at least 8 characters long");
        } else if (password.length() >= 12) {
            score += 30;
        } else {
            score += 20;
        }

        // Check for numbers
        if (!password.matches(".*\\d.*")) {
            suggestions.add("Add numbers to make your password stronger");
        } else {
            score += 20;
        }

        // Check for uppercase letters
        if (!password.matches(".*[A-Z].*")) {
            suggestions.add("Add uppercase letters to make your password stronger");
        } else {
            score += 20;
        }

        // Check for lowercase letters
        if (!password.matches(".*[a-z].*")) {
            suggestions.add("Add lowercase letters to make your password stronger");
        } else {
            score += 15;
        }

        // Check for special characters
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            suggestions.add("Add special characters to make your password stronger");
        } else {
            score += 15;
        }

        // Check for repeating characters
        if (password.matches(".*(.)\\1{2,}.*")) {
            suggestions.add("Avoid using repeating characters");
            score -= 10;
        }

        // Ensure score stays within 0-100 range
        score = Math.max(0, Math.min(100, score));

        return new PasswordStrength(score, suggestions);
    }
} 