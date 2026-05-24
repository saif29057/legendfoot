package com.ecommerce.app.controller;

import com.ecommerce.app.dto.OrderDto;
import com.ecommerce.app.entity.User;
import com.ecommerce.app.service.OrderService;
import com.ecommerce.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserService userService;
    private final OrderService orderService;

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

        // Load statistics for dashboard
        long totalUsers = userService.getTotalUserCount();
        long adminCount = userService.getUserCountByRole(User.Role.ADMIN);
        long enabledUsers = userService.getEnabledUserCount();
        long disabledUsers = totalUsers - enabledUsers;

        model.addAttribute("pageTitle", "Admin Dashboard");
        model.addAttribute("currentPage", "admin-dashboard");
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("adminCount", adminCount);
        model.addAttribute("enabledUsers", enabledUsers);
        model.addAttribute("disabledUsers", disabledUsers);

        return "admin/dashboard";
    }

    /**
     * Displays admin users management page.
     *
     * This method shows user management interface
     * for administrative purposes with pagination.
     *
     * @param page the page number (0-indexed)
     * @param size the page size
     * @return name of admin users view template
     */
    @GetMapping("/users")
    public String users(
        Model model,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        log.debug("Loading admin users page - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userService.getAllUsers(pageable);

        model.addAttribute("pageTitle", "User Management");
        model.addAttribute("currentPage", "admin-users");
        model.addAttribute("users", users);

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
     * for administrative purposes with pagination.
     *
     * @param page the page number (0-indexed)
     * @param size the page size
     * @return name of admin orders view template
     */
    @GetMapping("/orders")
    public String orders(
        Model model,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        log.debug("Loading admin orders page - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDto> orders = orderService.getAllOrders(pageable);

        model.addAttribute("pageTitle", "Order Management");
        model.addAttribute("currentPage", "admin-orders");
        model.addAttribute("orders", orders);

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
