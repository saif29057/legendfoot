package com.ecommerce.app.exception;

/**
 * Exception thrown when a user is not found.
 * 
 * This exception follows SOLID principles:
 * - Single Responsibility: Handles only user not found scenarios
 * - Open/Closed: Open for extension with additional fields
 * - Liskov Substitution: Can be substituted with any UserNotFoundException implementation
 * - Interface Segregation: Provides only user not found functionality
 * - Dependency Inversion: Low coupling, depends only on standard exception classes
 * 
 * This exception is used throughout the application to provide
 * consistent error handling for missing user resources.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructs a new UserNotFoundException.
     * 
     * @param message detailed error message
     */
    public UserNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new UserNotFoundException with cause.
     * 
     * @param message detailed error message
     * @param cause underlying cause of exception
     */
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
