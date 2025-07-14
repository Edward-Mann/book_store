package com.bnpparibasfortis.book_store.controller;

import com.bnpparibasfortis.book_store.dto.ApiResponse;
import com.bnpparibasfortis.book_store.util.AppConstants;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Global Exception Handler Tests")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    @DisplayName("Should handle IllegalArgumentException with NOT_FOUND status")
    void shouldHandleIllegalArgumentExceptionWithNotFoundStatus() {
        
        IllegalArgumentException exception = new IllegalArgumentException("Customer not found");

        
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleIllegalArgumentException(exception);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Customer not found");
        assertThat(response.getBody().getData()).isNull();
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException with CONFLICT status for existing username")
    void shouldHandleIllegalArgumentExceptionWithConflictStatusForExistingUsername() {
        
        IllegalArgumentException exception = new IllegalArgumentException("Username already exists");

        
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleIllegalArgumentException(exception);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Username already exists");
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException with CONFLICT status for existing email")
    void shouldHandleIllegalArgumentExceptionWithConflictStatusForExistingEmail() {
        
        IllegalArgumentException exception = new IllegalArgumentException("Email already exists");

        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleIllegalArgumentException(exception);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Email already exists");
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException with BAD_REQUEST status for other messages")
    void shouldHandleIllegalArgumentExceptionWithBadRequestStatusForOtherMessages() {
        
        IllegalArgumentException exception = new IllegalArgumentException("Invalid input data");

        
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleIllegalArgumentException(exception);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid input data");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Customer not found",
        "User not found", 
        AppConstants.CUSTOMER_NOT_FOUND
    })
    @DisplayName("Should return NOT_FOUND status for not found messages")
    void shouldReturnNotFoundStatusForNotFoundMessages(String message) {
        
        IllegalArgumentException exception = new IllegalArgumentException(message);


        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleIllegalArgumentException(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Username already exists",
        "Email already exists",
        "Product already exists"
    })
    @DisplayName("Should return CONFLICT status for already exists messages")
    void shouldReturnConflictStatusForAlreadyExistsMessages(String message) {
        
        IllegalArgumentException exception = new IllegalArgumentException(message);

        
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleIllegalArgumentException(exception);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("Should handle AuthenticationException")
    void shouldHandleAuthenticationException() {
        
        AuthenticationException exception = new AuthenticationException("Invalid credentials") {};

        
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleAuthenticationException(exception);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("User not authenticated");
        assertThat(response.getBody().getData()).isNull();
    }

    @Test
    @DisplayName("Should handle ResponseStatusException")
    void shouldHandleResponseStatusException() {
        
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");

        
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleResponseStatusException(exception);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Access denied");
        assertThat(response.getBody().getData()).isNull();
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with single field error")
    void shouldHandleMethodArgumentNotValidExceptionWithSingleFieldError() {
        
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("customer", "email", "Email is required");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleValidationException(exception);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("email: Email is required");
        assertThat(response.getBody().getData()).isNull();
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with multiple field errors")
    void shouldHandleMethodArgumentNotValidExceptionWithMultipleFieldErrors() {
        
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("customer", "email", "Email is required");
        FieldError fieldError2 = new FieldError("customer", "name", "Name is required");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleValidationException(exception);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).contains("email: Email is required");
        assertThat(response.getBody().getMessage()).contains("name: Name is required");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with no field errors")
    void shouldHandleMethodArgumentNotValidExceptionWithNoFieldErrors() {
        
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleValidationException(exception);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Validation failed");
    }

    @Test
    @DisplayName("Should handle ConstraintViolationException")
    void shouldHandleConstraintViolationException() {
        
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        Path propertyPath = mock(Path.class);
        
        when(violation.getPropertyPath()).thenReturn(propertyPath);
        when(propertyPath.toString()).thenReturn("customerId");
        when(violation.getMessage()).thenReturn("must be positive");
        
        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        violations.add(violation);
        
        ConstraintViolationException exception = new ConstraintViolationException(violations);

        
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleConstraintViolationException(exception);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("customerId: must be positive");
    }

    @Test
    @DisplayName("Should handle ConstraintViolationException with no violations")
    void shouldHandleConstraintViolationExceptionWithNoViolations() {
        
        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        ConstraintViolationException exception = new ConstraintViolationException(violations);

        
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleConstraintViolationException(exception);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Validation failed");
    }

    @Test
    @DisplayName("Should handle generic Exception")
    void shouldHandleGenericException() {
        
        Exception exception = new RuntimeException("Unexpected error occurred");

        
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleGenericException(exception);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred: Unexpected error occurred");
        assertThat(response.getBody().getData()).isNull();
    }

    @Test
    @DisplayName("Should handle generic Exception with null message")
    void shouldHandleGenericExceptionWithNullMessage() {
        
        Exception exception = new RuntimeException();

        
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleGenericException(exception);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).contains("An unexpected error occurred:");
    }
}