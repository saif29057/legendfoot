package com.ecommerce.app.service;

import com.ecommerce.app.entity.Order;
import com.ecommerce.app.entity.OrderItem;
import com.ecommerce.app.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for Order business operations.
 * 
 * This interface follows Single Responsibility Principle by defining only
 * order-related business operations. It also follows the Interface
 * Segregation Principle by providing only methods relevant to order management.
 * 
 * The interface uses dependency inversion by allowing implementations to be
 * injected without depending on concrete classes.
 */
public interface OrderService {

    /**
     * Creates a new order from user's cart.
     * 
     * This method handles order creation including cart validation,
     * stock reduction, and order item creation. It follows
     * Single Responsibility Principle by focusing only on order creation.
     * 
     * @param user user creating the order
     * @return created order
     * @throws IllegalArgumentException if cart is invalid or empty
     * @throws RuntimeException if checkout fails
     */
    Order createOrderFromCart(User user);

    /**
     * Creates an order with specific items.
     * 
     * This method handles direct order creation without using
     * cart, useful for admin orders or special cases.
     * 
     * @param user      user creating the order
     * @param orderItems list of order items
     * @return created order
     * @throws IllegalArgumentException if order items are invalid
     */
    Order createOrderWithItems(User user, List<OrderItem> orderItems);

    /**
     * Updates an existing order.
     * 
     * This method handles order updates while maintaining
     * business rules and data integrity.
     * 
     * @param id    ID of order to update
     * @param order updated order data
     * @return updated order
     * @throws IllegalArgumentException if order data is invalid
     * @throws RuntimeException if order not found or cannot be updated
     */
    Order updateOrder(Long id, Order order);

    /**
     * Cancels an order.
     * 
     * This method handles order cancellation with proper
     * stock restoration and status updates.
     * 
     * @param id ID of order to cancel
     * @return cancelled order
     * @throws RuntimeException if order not found or cannot be cancelled
     */
    Order cancelOrder(Long id);

    /**
     * Ships an order.
     * 
     * This method handles order shipping with tracking
     * number assignment and status updates.
     * 
     * @param id             ID of order to ship
     * @param trackingNumber  tracking number for shipment
     * @return shipped order
     * @throws RuntimeException if order not found or cannot be shipped
     */
    Order shipOrder(Long id, String trackingNumber);

    /**
     * Marks an order as delivered.
     * 
     * This method handles order delivery confirmation
     * and final status updates.
     * 
     * @param id ID of order to deliver
     * @return delivered order
     * @throws RuntimeException if order not found or cannot be delivered
     */
    Order deliverOrder(Long id);

    /**
     * Retrieves an order by its ID.
     * 
     * @param id ID of order to retrieve
     * @return OrderDto object if found, null otherwise
     */
    com.ecommerce.app.dto.OrderDto getOrderById(Long id);

    /**
     * Retrieves orders for a specific user.
     * 
     * @param user user whose orders to retrieve
     * @return List of orders for the user
     */
    List<Order> getOrdersByUser(User user);

    /**
     * Retrieves orders for a specific user with pagination.
     * 
     * @param user     user whose orders to retrieve
     * @param pageable pagination information
     * @return Page of orders for the user
     */
    Page<Order> getOrdersByUser(User user, Pageable pageable);

    /**
     * Retrieves orders for the current authenticated user with pagination.
     * 
     * This method retrieves orders for the currently authenticated user
     * and returns them as DTOs for safe controller layer usage.
     * 
     * @param pageable pagination information
     * @return Page of OrderDto objects for the current user
     */
    Page<com.ecommerce.app.dto.OrderDto> getUserOrders(Pageable pageable);

    /**
     * Retrieves all orders with pagination for admin use.
     * 
     * This method retrieves all orders in the system
     * and returns them as DTOs for admin dashboard usage.
     * 
     * @param pageable pagination information
     * @return Page of OrderDto objects for all orders
     */
    Page<com.ecommerce.app.dto.OrderDto> getAllOrders(Pageable pageable);

    
    /**
     * Retrieves orders by their status.
     * 
     * @param status order status to filter by
     * @return List of orders with specified status
     */
    List<Order> getOrdersByStatus(Order.OrderStatus status);

    /**
     * Retrieves orders by status with pagination.
     * 
     * @param status  order status to filter by
     * @param pageable pagination information
     * @return Page of orders with specified status
     */
    Page<Order> getOrdersByStatus(Order.OrderStatus status, Pageable pageable);

    /**
     * Retrieves orders placed within a date range.
     * 
     * @param startDate start of date range (inclusive)
     * @param endDate   end of date range (inclusive)
     * @return List of orders placed within date range
     */
    List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Retrieves orders by date range with pagination.
     * 
     * @param startDate start of date range (inclusive)
     * @param endDate   end of date range (inclusive)
     * @param pageable pagination information
     * @return Page of orders placed within date range
     */
    Page<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Retrieves recent orders.
     * 
     * @param pageable pagination information
     * @return Page of recent orders
     */
    Page<Order> getRecentOrders(Pageable pageable);

    /**
     * Retrieves recent orders for a specific user.
     * 
     * @param user     user whose recent orders to retrieve
     * @param pageable pagination information
     * @return Page of recent orders for the user
     */
    Page<Order> getRecentOrdersByUser(User user, Pageable pageable);

    /**
     * Retrieves orders that can be cancelled.
     * 
     * @return List of cancellable orders (PENDING status)
     */
    List<Order> getCancellableOrders();

    /**
     * Retrieves orders that can be shipped.
     * 
     * @return List of shippable orders (PENDING status)
     */
    List<Order> getShippableOrders();

    /**
     * Retrieves orders that can be delivered.
     * 
     * @return List of deliverable orders (SHIPPED status)
     */
    List<Order> getDeliverableOrders();

    /**
     * Calculates total revenue for a date range.
     * 
     * This method provides financial statistics for
     * reporting and business analysis.
     * 
     * @param startDate start of date range (inclusive)
     * @param endDate   end of date range (inclusive)
     * @return total revenue for the date range
     */
    BigDecimal calculateRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Calculates total revenue by order status.
     * 
     * @param status order status to calculate revenue for
     * @return total revenue for orders with specified status
     */
    BigDecimal calculateRevenueByStatus(Order.OrderStatus status);

    /**
     * Counts orders by status.
     * 
     * @param status order status to count
     * @return number of orders with specified status
     */
    long countOrdersByStatus(Order.OrderStatus status);

    /**
     * Counts orders for a specific user.
     * 
     * @param user user whose orders to count
     * @return number of orders for the user
     */
    long countOrdersByUser(User user);

    /**
     * Counts orders placed within a date range.
     * 
     * @param startDate start of date range (inclusive)
     * @param endDate   end of date range (inclusive)
     * @return number of orders placed within date range
     */
    long countOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Retrieves orders with tracking numbers.
     * 
     * @return List of orders that have been shipped with tracking
     */
    List<Order> getOrdersWithTrackingNumbers();

    /**
     * Retrieves shipped orders without tracking numbers.
     * 
     * @return List of shipped orders that need tracking numbers
     */
    List<Order> getShippedOrdersWithoutTrackingNumbers();

    /**
     * Updates order status.
     * 
     * This method provides controlled status updates
     * for order management workflows.
     * 
     * @param id     ID of order to update
     * @param status new status to set
     * @return updated order
     * @throws RuntimeException if order not found or status transition is invalid
     */
    Order updateOrderStatus(Long id, Order.OrderStatus status);

    /**
     * Adds notes to an order.
     * 
     * This method is useful for customer service
     * and order management communications.
     * 
     * @param id    ID of order to add notes to
     * @param notes notes to add
     * @return updated order
     * @throws RuntimeException if order not found
     */
    Order addOrderNotes(Long id, String notes);

    /**
     * Updates shipping address for an order.
     * 
     * @param id              ID of order to update
     * @param shippingAddress new shipping address
     * @return updated order
     * @throws RuntimeException if order not found or cannot be updated
     */
    Order updateShippingAddress(Long id, String shippingAddress);

    /**
     * Updates billing address for an order.
     * 
     * @param id            ID of order to update
     * @param billingAddress new billing address
     * @return updated order
     * @throws RuntimeException if order not found or cannot be updated
     */
    Order updateBillingAddress(Long id, String billingAddress);

    /**
     * Retrieves order items for a specific order.
     * 
     * @param orderId ID of order whose items to retrieve
     * @return List of order items
     */
    List<OrderItem> getOrderItems(Long orderId);

    /**
     * Validates order data before creation or update.
     * 
     * This method performs comprehensive validation of order data
     * according to business rules and constraints.
     * 
     * @param order order to validate
     * @return true if valid, false otherwise
     */
    boolean validateOrderData(Order order);

    /**
     * Retrieves order statistics for dashboard.
     * 
     * This method provides comprehensive statistics
     * for administrative dashboards and reporting.
     * 
     * @return OrderStatistics object with various metrics
     */
    OrderStatistics getOrderStatistics();

    /**
     * Inner class to hold order statistics.
     * 
     * This class follows Single Responsibility Principle by
     * containing only statistical data about orders.
     */
    class OrderStatistics {
        private long totalOrders;
        private long pendingOrders;
        private long shippedOrders;
        private long deliveredOrders;
        private long cancelledOrders;
        private BigDecimal totalRevenue;
        private BigDecimal averageOrderValue;

        // Constructors, getters, and setters
        public OrderStatistics() {}

        public OrderStatistics(long totalOrders, long pendingOrders, long shippedOrders, 
                          long deliveredOrders, long cancelledOrders, 
                          BigDecimal totalRevenue, BigDecimal averageOrderValue) {
            this.totalOrders = totalOrders;
            this.pendingOrders = pendingOrders;
            this.shippedOrders = shippedOrders;
            this.deliveredOrders = deliveredOrders;
            this.cancelledOrders = cancelledOrders;
            this.totalRevenue = totalRevenue;
            this.averageOrderValue = averageOrderValue;
        }

        // Getters and setters
        public long getTotalOrders() { return totalOrders; }
        public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }

        public long getPendingOrders() { return pendingOrders; }
        public void setPendingOrders(long pendingOrders) { this.pendingOrders = pendingOrders; }

        public long getShippedOrders() { return shippedOrders; }
        public void setShippedOrders(long shippedOrders) { this.shippedOrders = shippedOrders; }

        public long getDeliveredOrders() { return deliveredOrders; }
        public void setDeliveredOrders(long deliveredOrders) { this.deliveredOrders = deliveredOrders; }

        public long getCancelledOrders() { return cancelledOrders; }
        public void setCancelledOrders(long cancelledOrders) { this.cancelledOrders = cancelledOrders; }

        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

        public BigDecimal getAverageOrderValue() { return averageOrderValue; }
        public void setAverageOrderValue(BigDecimal averageOrderValue) { this.averageOrderValue = averageOrderValue; }
    }
}
