package com.brainridge_banking.api.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class EmailValidator {
    // Basic email regex pattern
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    // Set of common valid domains for simple validation
    private static final Set<String> VALID_DOMAINS = new HashSet<>(Arrays.asList(
            "gmail.com", "yahoo.com", "hotmail.com", "outlook.com",
            "aol.com", "icloud.com", "protonmail.com", "mail.com",
            "zoho.com", "yandex.com", "gmx.com", "live.com"
    ));

    /**
     * Validates if the email follows basic format and has a commonly used domain
     *
     * @param email The email to validate
     * @return true if the email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // Check basic format
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return false;
        }

        // Extract domain part
        String domain = email.substring(email.lastIndexOf('@') + 1).toLowerCase();

        // Check domain against common domains
        return VALID_DOMAINS.contains(domain);
    }

    /**
     * Get error message for invalid email
     *
     * @param email The email that failed validation
     * @return Descriptive error message
     */
    public static String getInvalidEmailMessage(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email cannot be empty";
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "Email format is invalid: " + email;
        }

        String domain = email.substring(email.lastIndexOf('@') + 1).toLowerCase();
        return "Email domain is not supported: " + domain + ". Please use a common email provider.";
    }
}
