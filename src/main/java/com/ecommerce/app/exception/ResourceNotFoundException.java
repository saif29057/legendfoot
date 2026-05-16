package com.ecommerce.app.exception;

/**
 * Exception thrown when a requested resource is not found.
 * 
 * This exception follows SOLID principles:
 * - Single Responsibility: Handles only resource not found scenarios
 * - Open/Closed: Open for extension with additional fields
 * - Liskov Substitution: Can be substituted with any ResourceNotFoundException implementation
 * - Interface Segregation: Provides only resource not found functionality
 * - Dependency Inversion: Low coupling, depends only on standard exception classes
 * 
 * This exception is used throughout the application to provide
 * consistent error handling for missing resources.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException.
     * 
     * @param message detailed error message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new ResourceNotFoundException with cause.
     * 
     * @param message detailed error message
     * @param cause underlying cause of exception
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
