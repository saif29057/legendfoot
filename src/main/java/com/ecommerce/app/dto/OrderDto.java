package com.ecommerce.app.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Order operations.
 *
 * This DTO follows the Single Responsibility Principle by containing
 * only order-related data for transfer between layers.
 * It prevents entity exposure and provides clean separation
 * between controller and service layers.
 *
 * The DTO provides a clean way to transfer order data
 * and follows best practices for data transfer objects.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    /**
     * Order ID for update operations.
     * Can be null for create operations.
     */
    private Long id;

    /**
     * User ID associated with the order.
     * Used for order ownership verification.
     */
    private Long userId;

    /**
     * Username of the customer who placed the order.
     * Used for order display in admin panels.
     */
    private String userName;

    /**
     * Email of the customer who placed the order.
     * Used for order display and customer contact.
     */
    private String userEmail;

    /**
     * Order date for display and filtering.
     * Set automatically on order creation.
     */
    private LocalDateTime orderDate;

    /**
     * Total price of the order.
     * Calculated from order items.
     */
    private java.math.BigDecimal totalPrice;

    /**
     * Current status of the order.
     * Used for order tracking and management.
     */
    private String status;

    /**
     * Shipping address for delivery.
     * Can be null for digital products.
     */
    private String shippingAddress;

    /**
     * Billing address for payment.
     * Can be null if same as shipping.
     */
    private String billingAddress;

    /**
     * Tracking number for shipment tracking.
     * Set when order is shipped.
     */
    private String trackingNumber;

    /**
     * Additional notes for the order.
     * Used for customer service and special instructions.
     */
    private String notes;

    /**
     * List of order items in the order.
     * Used for order details and display.
     */
    private List<OrderItemDto> items;

    /**
     * Static factory method for creating OrderDto from Order entity.
     *
     * This method provides clean conversion between entity and DTO,
     * following the Single Responsibility Principle by centralizing
     * conversion logic.
     *
     * @param order Order entity to convert
     * @return OrderDto with order data
     */
    public static OrderDto fromEntity(com.ecommerce.app.entity.Order order) {
        if (order == null) {
            return null;
        }

        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setUserId(order.getUser() != null ? order.getUser().getId() : null);
        dto.setUserName(
            order.getUser() != null ? order.getUser().getUsername() : null
        );
        dto.setUserEmail(
            order.getUser() != null ? order.getUser().getEmail() : null
        );
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setStatus(
            order.getStatus() != null ? order.getStatus().name() : null
        );
        dto.setShippingAddress(order.getShippingAddress());
        dto.setBillingAddress(order.getBillingAddress());
        dto.setTrackingNumber(order.getTrackingNumber());
        dto.setNotes(order.getNotes());

        // Convert order items only if they are loaded
        // Order items are LAZY loaded, so check if they're initialized
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            List<OrderItemDto> itemDtos = order
                .getOrderItems()
                .stream()
                .map(OrderItemDto::fromEntity)
                .collect(java.util.stream.Collectors.toList());
            dto.setItems(itemDtos);
        } else {
            dto.setItems(new java.util.ArrayList<>());
        }

        return dto;
    }

    /**
     * Static factory method for creating OrderDto for checkout confirmation.
     *
     * This method provides a clean way to create DTOs
     * for checkout confirmation pages.
     *
     * @param order Order entity to convert
     * @return OrderDto with checkout confirmation data
     */
    public static OrderDto forCheckoutConfirmation(
        com.ecommerce.app.entity.Order order
    ) {
        OrderDto dto = fromEntity(order);

        // You could add additional fields specific to checkout confirmation
        // For example: payment method, delivery estimates, etc.

        return dto;
    }

    /**
     * Static factory method for creating OrderDto for order history.
     *
     * This method provides a clean way to create DTOs
     * for order history display.
     *
     * @param order Order entity to convert
     * @return OrderDto with order history data
     */
    public static OrderDto forOrderHistory(
        com.ecommerce.app.entity.Order order
    ) {
        OrderDto dto = fromEntity(order);

        // You could add fields specific to order history display
        // For example: simplified item list, status badges, etc.

        return dto;
    }
}
