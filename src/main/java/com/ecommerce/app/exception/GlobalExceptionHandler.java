package com.ecommerce.app.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Global exception handler for the application.
 *
 * This class follows SOLID principles:
 * - Single Responsibility: Handles only global exception processing
 * - Open/Closed: Open for extension through additional exception types
 * - Liskov Substitution: Can be substituted with any GlobalExceptionHandler implementation
 * - Interface Segregation: Provides only exception handling functionality
 * - Dependency Inversion: Low coupling, depends only on Spring MVC components
 *
 * This class provides centralized exception handling across the entire application,
 * ensuring consistent error responses and user feedback.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException.
     *
     * This method catches resource not found exceptions
     * and returns appropriate error page with 404 status.
     *
     * @param ex the ResourceNotFoundException
     * @param model for view attributes
     * @return ModelAndView with error page
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleResourceNotFoundException(
            ResourceNotFoundException ex, Model model) {

        log.error("Resource not found: {}", ex.getMessage(), ex);

        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("statusCode", HttpStatus.NOT_FOUND.value());
        model.addAttribute("pageTitle", "Resource Not Found");

        return new ModelAndView("error/404", model.asMap());
    }

    /**
     * Handles UserNotFoundException.
     *
     * This method catches user not found exceptions
     * and returns appropriate error page with 404 status.
     *
     * @param ex the UserNotFoundException
     * @param model for view attributes
     * @return ModelAndView with error page
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleUserNotFoundException(
            UserNotFoundException ex, Model model) {

        log.error("User not found: {}", ex.getMessage(), ex);

        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("statusCode", HttpStatus.NOT_FOUND.value());
        model.addAttribute("pageTitle", "User Not Found");

        return new ModelAndView("error/404", model.asMap());
    }

    /**
     * Handles BusinessException.
     *
     * This method catches business logic exceptions
     * and returns appropriate error page with 400 status.
     *
     * @param ex the BusinessException
     * @param model for view attributes
     * @return ModelAndView with error page
     */
    @ExceptionHandler(BusinessException.class)
    public ModelAndView handleBusinessException(
            BusinessException ex, Model model) {

        log.error("Business logic error: {}", ex.getMessage(), ex);

        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("pageTitle", "Business Error");

        return new ModelAndView("error/400", model.asMap());
    }

    /**
     * Handles IllegalArgumentException.
     *
     * This method catches illegal argument exceptions
     * and returns appropriate error page with 400 status.
     *
     * @param ex the IllegalArgumentException
     * @param model for view attributes
     * @return ModelAndView with error page
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgumentException(
            IllegalArgumentException ex, Model model) {

        log.error("Illegal argument: {}", ex.getMessage(), ex);

        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("pageTitle", "Invalid Request");

        return new ModelAndView("error/400", model.asMap());
    }

    /**
     * Handles HttpRequestMethodNotSupportedException.
     *
     * Occurs when a POST-only endpoint (e.g. /cart/add) receives a GET request.
     * Returning a 500 page for this is misleading; redirect to the home page instead.
     *
     * @param ex    the HttpRequestMethodNotSupportedException
     * @param model for view attributes
     * @return redirect to home
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ModelAndView handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, Model model) {

        log.warn("Method not supported: {} {}", ex.getMethod(), ex.getMessage());

        model.addAttribute("errorMessage",
                "The action you attempted is not available via this method.");
        model.addAttribute("statusCode", HttpStatus.METHOD_NOT_ALLOWED.value());
        model.addAttribute("pageTitle", "Method Not Allowed");

        ModelAndView mav = new ModelAndView("error/404", model.asMap());
        mav.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
        return mav;
    }

    /**
     * Handles NoResourceFoundException (e.g. missing static files like favicon.ico).
     *
     * Without this handler the generic Exception catch-all would incorrectly
     * return an HTTP 500 page for what is actually a 404 condition.
     *
     * @param ex    the NoResourceFoundException
     * @param model for view attributes
     * @return ModelAndView with 404 error page
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ModelAndView handleNoResourceFoundException(
            NoResourceFoundException ex, Model model) {

        log.warn("Static resource not found: {}", ex.getMessage());

        model.addAttribute("errorMessage", "The requested resource could not be found.");
        model.addAttribute("statusCode", HttpStatus.NOT_FOUND.value());
        model.addAttribute("pageTitle", "Not Found");

        ModelAndView mav = new ModelAndView("error/404", model.asMap());
        mav.setStatus(HttpStatus.NOT_FOUND);
        return mav;
    }

    /**
     * Handles generic exceptions.
     *
     * This method catches all other exceptions
     * and returns appropriate error page with 500 status.
     *
     * @param ex    the generic Exception
     * @param model for view attributes
     * @return ModelAndView with error page
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(
            Exception ex, Model model) {

        log.error("Unexpected error: {}", ex.getMessage(), ex);

        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again.");
        model.addAttribute("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("pageTitle", "Server Error");

        ModelAndView mav = new ModelAndView("error/500", model.asMap());
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return mav;
    }
}
