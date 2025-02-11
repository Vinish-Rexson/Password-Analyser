package com.passwordanalyzer.core;

import java.security.SecureRandom;

public class PasswordGenerator {
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    
    private final SecureRandom random = new SecureRandom();

    public String generatePassword(int length, boolean useLower, boolean useUpper, 
                                 boolean useDigits, boolean useSpecial) {
        StringBuilder password = new StringBuilder();
        String validChars = "";

        if (useLower) validChars += LOWER;
        if (useUpper) validChars += UPPER;
        if (useDigits) validChars += DIGITS;
        if (useSpecial) validChars += SPECIAL;

        if (validChars.isEmpty()) {
            validChars = LOWER + UPPER + DIGITS; // Default if nothing selected
        }

        // Ensure at least one character from each selected type
        if (useLower) password.append(LOWER.charAt(random.nextInt(LOWER.length())));
        if (useUpper) password.append(UPPER.charAt(random.nextInt(UPPER.length())));
        if (useDigits) password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        if (useSpecial) password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        // Fill the rest of the password
        while (password.length() < length) {
            password.append(validChars.charAt(random.nextInt(validChars.length())));
        }

        // Shuffle the password
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
    }
} 