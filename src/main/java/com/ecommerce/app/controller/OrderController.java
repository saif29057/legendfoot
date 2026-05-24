package com.ecommerce.app.controller;

import com.ecommerce.app.dto.OrderDto;
import com.ecommerce.app.entity.Order;
import com.ecommerce.app.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for handling order-related operations.
 *
 * This controller follows SOLID principles:
 * - Single Responsibility: Handles only order-related web requests
 * - Open/Closed: Open for extension through additional endpoints
 * - Liskov Substitution: Can be substituted with any OrderController implementation
 * - Interface Segregation: Provides only order-related methods
 * - Dependency Inversion: Depends on OrderService interface, not concrete classes
 *
 * The class provides a clean separation between web layer
 * and business logic, following best practices for controller design.
 */
@Controller
@RequestMapping("/orders")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Displays user's order history.
     *
     * This method shows all orders for the current user
     * with pagination support.
     *
     * @param model for view attributes
     * @param pageable for pagination
     * @return name of order list view template
     */
    @GetMapping
    public String listOrders(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.debug("Loading orders list - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDto> orders = orderService.getUserOrders(pageable);

        model.addAttribute("orders", orders);
        model.addAttribute("pageTitle", "My Orders");
        model.addAttribute("currentPage", "orders");

        return "orders/list";
    }

    /**
     * Admin: Displays all orders with pagination.
     *
     * This method shows all orders in the system
     * for administrative purposes.
     * Only accessible to users with ADMIN role.
     *
     * @param model for view attributes
     * @param pageable for pagination
     * @return name of admin order list view template
     */
    @GetMapping("/admin")
    public String listAllOrders(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.debug("Loading all orders for admin - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDto> orders = orderService.getAllOrders(pageable);

        model.addAttribute("orders", orders);
        model.addAttribute("pageTitle", "All Orders");
        model.addAttribute("currentPage", "admin-orders");

        return "orders/admin-list";
    }

    /**
     * Displays order details page.
     *
     * This method shows detailed information about a specific order.
     *
     * @param id order ID
     * @param model for view attributes
     * @return name of order details view template
     */
    @GetMapping("/{id}")
    public String showOrder(
            @PathVariable Long id,
            Model model) {

        log.debug("Loading order details for ID: {}", id);

        OrderDto order = orderService.getOrderById(id);
        if (order == null) {
            log.warn("Order not found with ID: {}", id);
            return "redirect:/orders?error=Order not found";
        }

        model.addAttribute("order", order);
        model.addAttribute("pageTitle", "Order #" + order.getId());
        model.addAttribute("currentPage", "orders");

        return "orders/details";
    }

    /**
     * Cancels an order.
     *
     * This method handles order cancellation with
     * appropriate validation and feedback to the user.
     *
     * @param id order ID
     * @param redirectAttributes for flash messages
     * @return redirect to order list on success
     */
    @PostMapping("/{id}/cancel")
    public String cancelOrder(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        log.debug("Cancelling order with ID: {}", id);

        try {
            orderService.cancelOrder(id);
            log.info("Order cancelled successfully: {}", id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Order cancelled successfully!");
            return "redirect:/orders";

        } catch (Exception e) {
            log.error("Failed to cancel order: {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to cancel order: " + e.getMessage());
            return "redirect:/orders";
        }
    }

    /**
     * Admin: Updates order status.
     *
     * This method handles order status updates for
     * administrative purposes with validation.
     * Only accessible to users with ADMIN role.
     *
     * @param id order ID
     * @param status new order status
     * @param redirectAttributes for flash messages
     * @return redirect to admin order list on success
     */
    @PostMapping("/admin/{id}/status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {

        log.debug("Updating order status - ID: {}, status: {}", id, status);

        try {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            orderService.updateOrderStatus(id, orderStatus);
            log.info("Order status updated successfully - ID: {}, status: {}", id, orderStatus);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Order status updated successfully!");
            return "redirect:/orders/admin";

        } catch (Exception e) {
            log.error("Failed to update order status - ID: {}, status: {}", id, status, e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to update order status: " + e.getMessage());
            return "redirect:/orders/admin";
        }
    }
}
