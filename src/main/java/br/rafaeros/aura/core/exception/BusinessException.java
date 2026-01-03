package br.rafaeros.aura.core.exception;

/** Exception for business rule violations (e.g., duplicate user, invalid operation). */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
