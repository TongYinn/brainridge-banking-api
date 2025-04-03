package com.brainridge_banking.api;

import com.brainridge_banking.api.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @Mock
    private WebRequest webRequest;

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        // Your GlobalExceptionHandler doesn't actually use webRequest.getDescription()
        // so we're removing this stub to fix the UnnecessaryStubbingException
    }

    @Test
    void handleResourceNotFoundException_ShouldReturnNotFoundStatus() {
        // Arrange
        String errorMessage = "Resource not found";
        ResourceNotFoundException ex = new ResourceNotFoundException(errorMessage);

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleResourceNotFoundException(ex, webRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(HttpStatus.NOT_FOUND.value(), responseBody.get("status"));
        assertEquals("Resource Not Found", responseBody.get("error"));
        assertEquals(errorMessage, responseBody.get("message"));
    }

    @Test
    void handleInsufficientFundsException_ShouldReturnBadRequestStatus() {
        // Arrange
        String errorMessage = "Insufficient funds";
        InsufficientFundsException ex = new InsufficientFundsException(errorMessage);

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleInsufficientFundsException(ex, webRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody.get("status"));
        assertEquals("Insufficient Funds", responseBody.get("error"));
        assertEquals(errorMessage, responseBody.get("message"));
    }

    @Test
    void handleDuplicateEmailException_ShouldReturnConflictStatus() {
        // Arrange
        String errorMessage = "Duplicate email";
        DuplicateEmailException ex = new DuplicateEmailException(errorMessage);

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleDuplicateEmailException(ex, webRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(HttpStatus.CONFLICT.value(), responseBody.get("status"));
        assertEquals("Duplicate Email", responseBody.get("error"));
        assertEquals(errorMessage, responseBody.get("message"));
    }

    @Test
    void handleInvalidEmailException_ShouldReturnBadRequestStatus() {
        // Arrange
        String errorMessage = "Invalid email";
        InvalidEmailException ex = new InvalidEmailException(errorMessage);

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleInvalidEmailException(ex, webRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody.get("status"));
        assertEquals("Invalid Email", responseBody.get("error"));
        assertEquals(errorMessage, responseBody.get("message"));
    }

    @Test
    void handleGlobalException_ShouldReturnInternalServerErrorStatus() {
        // Arrange
        String errorMessage = "Unexpected error";
        Exception ex = new Exception(errorMessage);

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleGlobalException(ex, webRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseBody.get("status"));
        assertEquals("Internal Server Error", responseBody.get("error"));
        assertEquals(errorMessage, responseBody.get("message"));
    }
}