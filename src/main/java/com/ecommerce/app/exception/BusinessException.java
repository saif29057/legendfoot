package com.ecommerce.app.exception;

/**
 * Base exception for business logic errors.
 * 
 * This exception follows SOLID principles:
 * - Single Responsibility: Handles only business logic exceptions
 * - Open/Closed: Open for extension with additional fields
 * - Liskov Substitution: Can be substituted with any BusinessException implementation
 * - Interface Segregation: Provides only business exception functionality
 * - Dependency Inversion: Low coupling, depends only on standard exception classes
 * 
 * This exception is used throughout the application to provide
 * consistent error handling for business rule violations.
 */
public class BusinessException extends RuntimeException {

    /**
     * Constructs a new BusinessException.
     * 
     * @param message detailed error message
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Constructs a new BusinessException with cause.
     * 
     * @param message detailed error message
     * @param cause underlying cause of exception
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
