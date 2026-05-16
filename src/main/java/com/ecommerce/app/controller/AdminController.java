package com.ecommerce.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for handling administrative dashboard and operations.
 * 
 * This controller follows SOLID principles:
 * - Single Responsibility: Handles only admin-related web requests
 * - Open/Closed: Open for extension through additional endpoints
 * - Liskov Substitution: Can be substituted with any AdminController implementation
 * - Interface Segregation: Provides only admin-related methods
 * - Dependency Inversion: Depends on Spring MVC components
 * 
 * The class provides a clean separation between web layer
 * and business logic, following best practices for controller design.
 */
@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    /**
     * Displays admin dashboard.
     * 
     * This method shows the main administrative dashboard
     * with system overview and quick actions.
     * 
     * @return name of admin dashboard view template
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        log.debug("Loading admin dashboard");
        
        // In a real application, you would load statistics
        // like total users, orders, products, revenue, etc.
        model.addAttribute("pageTitle", "Admin Dashboard");
        model.addAttribute("currentPage", "admin-dashboard");
        
        return "admin/dashboard";
    }

    /**
     * Displays admin users management page.
     * 
     * This method shows user management interface
     * for administrative purposes.
     * 
     * @return name of admin users view template
     */
    @GetMapping("/users")
    public String users(Model model) {
        log.debug("Loading admin users page");
        
        model.addAttribute("pageTitle", "User Management");
        model.addAttribute("currentPage", "admin-users");
        
        return "admin/users";
    }

    /**
     * Displays admin products management page.
     * 
     * This method shows product management interface
     * for administrative purposes.
     * 
     * @return name of admin products view template
     */
    @GetMapping("/products")
    public String products(Model model) {
        log.debug("Loading admin products page");
        
        model.addAttribute("pageTitle", "Product Management");
        model.addAttribute("currentPage", "admin-products");
        
        return "admin/products";
    }

    /**
     * Displays admin orders management page.
     * 
     * This method shows order management interface
     * for administrative purposes.
     * 
     * @return name of admin orders view template
     */
    @GetMapping("/orders")
    public String orders(Model model) {
        log.debug("Loading admin orders page");
        
        model.addAttribute("pageTitle", "Order Management");
        model.addAttribute("currentPage", "admin-orders");
        
        return "admin/orders";
    }

    /**
     * Displays admin reports page.
     * 
     * This method shows various reports and analytics
     * for administrative purposes.
     * 
     * @return name of admin reports view template
     */
    @GetMapping("/reports")
    public String reports(Model model) {
        log.debug("Loading admin reports page");
        
        model.addAttribute("pageTitle", "Reports & Analytics");
        model.addAttribute("currentPage", "admin-reports");
        
        return "admin/reports";
    }

    /**
     * Displays admin settings page.
     * 
     * This method shows system configuration and settings
     * for administrative purposes.
     * 
     * @return name of admin settings view template
     */
    @GetMapping("/settings")
    public String settings(Model model) {
        log.debug("Loading admin settings page");
        
        model.addAttribute("pageTitle", "System Settings");
        model.addAttribute("currentPage", "admin-settings");
        
        return "admin/settings";
    }
}
