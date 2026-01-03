package br.rafaeros.aura.core.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // --- BUSINESS RULES & VALIDATION (WARNING) ---

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                Severity.WARNING,
                "Business Rule Violation",
                ex.getMessage(),
                request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach(
                        (error) -> {
                            String fieldName = ((FieldError) error).getField();
                            String errorMessage = error.getDefaultMessage();
                            validationErrors.put(fieldName, errorMessage);
                        });

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("severity", Severity.WARNING.name());
        body.put("error", "Validation Error");
        body.put("message", "Please check the input fields.");
        body.put("path", request.getRequestURI());
        body.put("validationErrors", validationErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateEntry(
            DataIntegrityViolationException ex, HttpServletRequest request) {

        String message = "Data integrity violation.";

        if (ex.getMessage() != null && ex.getMessage().contains("constraint")) {
            message = "This record already exists in the system.";
        }

        return buildErrorResponse(
                HttpStatus.CONFLICT, Severity.WARNING, "Duplicate Entry", message, request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                Severity.WARNING,
                "Resource Not Found",
                ex.getMessage(),
                request);
    }

    // --- SECURITY & AUTH (WARNING/ERROR) ---

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {

        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                Severity.WARNING,
                "Forbidden",
                "Access denied: You do not have permission to access this resource.",
                request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {

        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                Severity.WARNING,
                "Unauthorized",
                "Authentication failed. Please check your credentials.",
                request);
    }

    // --- CRITICAL SYSTEM ERRORS (ERROR) ---

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, HttpServletRequest request) {

        ex.printStackTrace();

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                Severity.ERROR,
                "Internal Server Error",
                "An unexpected error occurred on the server: " + ex.getMessage(),
                request);
    }

    @ExceptionHandler(IntegrationException.class)
    public ResponseEntity<Map<String, Object>> handleIntegrationException(
            IntegrationException ex, HttpServletRequest request) {

        return buildErrorResponse(
                HttpStatus.BAD_GATEWAY,
                Severity.ERROR,
                "Integration Error",
                "External service failure: " + ex.getMessage(),
                request);
    }

    // --- HELPER METHOD ---

    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status,
            Severity severity,
            String errorType,
            String message,
            HttpServletRequest request) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("severity", severity.name()); // "WARNING", "ERROR", "INFO"
        body.put("error", errorType);
        body.put("message", message);
        body.put("path", request.getRequestURI());

        return ResponseEntity.status(status).body(body);
    }
}
