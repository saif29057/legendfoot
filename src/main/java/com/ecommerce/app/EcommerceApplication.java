package com.ecommerce.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the E-Commerce Application.
 * 
 * This class serves as the entry point for the Spring Boot application.
 * It contains the main method that starts the application context.
 * 
 * @SpringBootApplication annotation combines:
 * - @Configuration: Marks this class as a configuration class
 * - @EnableAutoConfiguration: Enables Spring Boot's auto-configuration
 * - @ComponentScan: Enables component scanning for Spring beans
 * 
 * This follows the Single Responsibility Principle by being only responsible
 * for starting the application.
 */
@SpringBootApplication
public class EcommerceApplication {

    /**
     * Main method that starts the Spring Boot application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }

}
