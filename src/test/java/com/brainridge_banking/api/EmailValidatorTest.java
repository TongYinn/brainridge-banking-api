package com.brainridge_banking.api;

import com.brainridge_banking.api.util.EmailValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class EmailValidatorTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "user@gmail.com",
            "test.user@yahoo.com",
            "john.doe123@hotmail.com",
            "jane_doe@outlook.com",
            "user+tag@gmail.com"
    })
    void isValidEmail_WithValidEmails_ShouldReturnTrue(String email) {
        // Act & Assert
        assertTrue(EmailValidator.isValidEmail(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-email",
            "user@nonexistentdomain.com",
            "user@",
            "@gmail.com",
            "user@gmail..com",
            "user@.com",
            "user@yahoo",
            "user@-gmail.com",
            "user@gmail-.com"
    })
    void isValidEmail_WithInvalidEmails_ShouldReturnFalse(String email) {
        // Act & Assert
        assertFalse(EmailValidator.isValidEmail(email));
    }

    @Test
    void isValidEmail_WithNullEmail_ShouldReturnFalse() {
        // Act & Assert
        assertFalse(EmailValidator.isValidEmail(null));
    }

    @Test
    void isValidEmail_WithEmptyEmail_ShouldReturnFalse() {
        // Act & Assert
        assertFalse(EmailValidator.isValidEmail(""));
        assertFalse(EmailValidator.isValidEmail("  "));
    }

    @Test
    void getInvalidEmailMessage_WithNullEmail_ShouldReturnAppropriateMessage() {
        // Act
        String message = EmailValidator.getInvalidEmailMessage(null);

        // Assert
        assertEquals("Email cannot be empty", message);
    }

    @Test
    void getInvalidEmailMessage_WithEmptyEmail_ShouldReturnAppropriateMessage() {
        // Act
        String message = EmailValidator.getInvalidEmailMessage("");

        // Assert
        assertEquals("Email cannot be empty", message);
    }

    @Test
    void getInvalidEmailMessage_WithInvalidFormat_ShouldReturnAppropriateMessage() {
        // Arrange
        String invalidEmail = "invalid-email";

        // Act
        String message = EmailValidator.getInvalidEmailMessage(invalidEmail);

        // Assert
        assertEquals("Email format is invalid: " + invalidEmail, message);
    }

    @Test
    void getInvalidEmailMessage_WithUnsupportedDomain_ShouldReturnAppropriateMessage() {
        // Arrange
        String email = "user@unsupported.com";
        String domain = "unsupported.com";

        // Act
        String message = EmailValidator.getInvalidEmailMessage(email);

        // Assert
        assertEquals("Email domain is not supported: " + domain + ". Please use a common email provider.", message);
    }
}
