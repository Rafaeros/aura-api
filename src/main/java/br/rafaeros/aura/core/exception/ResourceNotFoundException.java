package br.rafaeros.aura.core.exception;

/**
 * Exception for resources that are not found (e.g., User ID 999).
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}