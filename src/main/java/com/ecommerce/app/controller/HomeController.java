package com.ecommerce.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for handling home page and general requests.
 * 
 * This controller follows SOLID principles:
 * - Single Responsibility: Handles only home page requests
 * - Open/Closed: Open for extension through additional endpoints
 * - Liskov Substitution: Can be substituted with any HomeController implementation
 * - Interface Segregation: Provides only home page related methods
 * - Dependency Inversion: Depends on Spring MVC components
 * 
 * The class provides a clean separation between web layer
 * and business logic, following best practices for controller design.
 */
@Controller
@Slf4j
public class HomeController {

    /**
     * Handles requests to the home page.
     * 
     * This method displays the main landing page with
     * featured products and navigation options.
     * 
     * @return the name of the home view template
     */
    @GetMapping("/")
    public String home(Model model) {
        log.debug("Loading home page");
        
        // Add common attributes for all pages
        model.addAttribute("pageTitle", "Welcome to E-Commerce");
        model.addAttribute("currentPage", "home");
        
        return "home";
    }

    /**
     * Handles requests to the about page.
     * 
     * This method displays information about the e-commerce platform.
     * 
     * @return the name of the about view template
     */
    @GetMapping("/about")
    public String about(Model model) {
        log.debug("Loading about page");
        
        model.addAttribute("pageTitle", "About E-Commerce");
        model.addAttribute("currentPage", "about");
        
        return "about";
    }

    /**
     * Handles requests to the contact page.
     * 
     * This method displays contact information and form.
     * 
     * @return the name of the contact view template
     */
    @GetMapping("/contact")
    public String contact(Model model) {
        log.debug("Loading contact page");
        
        model.addAttribute("pageTitle", "Contact Us");
        model.addAttribute("currentPage", "contact");
        
        return "contact";
    }
}
