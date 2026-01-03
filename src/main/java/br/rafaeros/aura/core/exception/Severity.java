package br.rafaeros.aura.core.exception;

public enum Severity {
    ERROR, // Critical system failures (Red)
    WARNING, // Business rules, validation, or recoverable errors (Yellow/Amber)
    INFO // Informational messages (Blue/Green)
}
