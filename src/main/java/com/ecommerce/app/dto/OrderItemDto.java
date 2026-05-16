package com.ecommerce.app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for OrderItem operations.
 * 
 * This DTO follows the Single Responsibility Principle by containing
 * only order item-related data for transfer between layers.
 * It prevents entity exposure and provides clean separation
 * between controller and service layers.
 * 
 * The DTO uses validation annotations to ensure data integrity
 * and follows best practices for data transfer objects.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {

    /**
     * Order item ID for update operations.
     * Can be null for create operations.
     */
    private Long id;

    /**
     * Order ID for reference and display.
     * Used to identify the order this item belongs to.
     */
    @NotNull(message = "Order ID is required")
    private Long orderId;

    /**
     * Product ID for reference and display.
     * Used to identify the product in this order item.
     */
    @NotNull(message = "Product ID is required")
    private Long productId;

    /**
     * Product name for display purposes.
     * Used for order item display without exposing product entity.
     */
    private String productName;

    /**
     * Product image URL for display.
     * Used for order item visualization.
     */
    private String productImageUrl;

    /**
     * Quantity of the product in the order.
     * Must be positive integer.
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    /**
     * Unit price of the product at the time of order.
     * Used for price calculations and historical records.
     */
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;

    /**
     * Subtotal price for this order item (quantity × unit price).
     * Calculated field for display and order totals.
     */
    private BigDecimal subtotal;

    /**
     * Static factory method for creating OrderItemDto from OrderItem entity.
     * 
     * This method provides clean conversion between entity and DTO,
     * following the Single Responsibility Principle by centralizing
     * conversion logic.
     * 
     * @param orderItem OrderItem entity to convert
     * @return OrderItemDto with order item data
     */
    public static OrderItemDto fromEntity(com.ecommerce.app.entity.OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        
        OrderItemDto dto = new OrderItemDto();
        dto.setId(orderItem.getId());
        dto.setOrderId(orderItem.getOrder() != null ? orderItem.getOrder().getId() : null);
        dto.setProductId(orderItem.getProduct() != null ? orderItem.getProduct().getId() : null);
        dto.setProductName(orderItem.getDisplayName());
        dto.setProductImageUrl(orderItem.getImageUrl());
        dto.setQuantity(orderItem.getQuantity());
        dto.setUnitPrice(orderItem.getPrice());
        dto.setSubtotal(orderItem.getSubtotal());
        
        return dto;
    }

    /**
     * Static factory method for creating OrderItemDto for order creation.
     * 
     * This method provides a clean way to create DTOs
     * for order item creation operations.
     * 
     * @param orderId     order ID for the item
     * @param productId    product ID for the item
     * @param productName  product name for display
     * @param productImageUrl product image URL
     * @param unitPrice     product unit price
     * @param quantity     product quantity
     * @return OrderItemDto with creation data
     */
    public static OrderItemDto forCreation(Long orderId, Long productId, String productName, 
                                                String productImageUrl, BigDecimal unitPrice, 
                                                Integer quantity) {
        OrderItemDto dto = new OrderItemDto();
        dto.setId(null); // New order item
        dto.setOrderId(orderId);
        dto.setProductId(productId);
        dto.setProductName(productName);
        dto.setProductImageUrl(productImageUrl);
        dto.setUnitPrice(unitPrice);
        dto.setQuantity(quantity);
        dto.setSubtotal(unitPrice.multiply(BigDecimal.valueOf(quantity)));
        
        return dto;
    }
}
