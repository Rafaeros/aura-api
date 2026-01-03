package br.rafaeros.aura.core.exception;

/**
 * Exception for external service failures (Everynet API, MQTT Broker, S3, etc).
 */
public class IntegrationException extends RuntimeException {
    public IntegrationException(String message) {
        super(message);
    }
}