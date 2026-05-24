package com.ecommerce.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OrderItem entity representing an item in a customer order.
 *
 * This entity follows the Single Responsibility Principle by being responsible
 * only for order item data and relationships. It's a JPA entity that maps
 * to the 'order_items' table in the database.
 *
 * The order item represents a product with a specific quantity and price
 * at the time of order placement. The price is stored to preserve historical
 * pricing even if the product price changes later.
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"order", "product"})
@ToString(exclude = {"order", "product"})
public class OrderItem {

    /**
     * Primary key for the order item entity.
     * Uses GenerationType.IDENTITY for auto-increment in MySQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Many-to-one relationship with Order entity.
     * Each order item belongs to exactly one order.
     * Fetch type is LAZY to avoid loading order data when not needed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * Many-to-one relationship with Product entity.
     * Each order item references exactly one product.
     * Fetch type is LAZY to avoid loading product data when not needed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Quantity of the product ordered.
     * Must be positive.
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * Price of the product at the time of order placement.
     * Stored to preserve historical pricing regardless of future price changes.
     */
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Timestamp when the order item was created.
     * Automatically set on creation.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the order item was last updated.
     * Automatically updated on any field change.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Pre-persist callback to set creation timestamp.
     * Automatically called by JPA before entity is saved.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Pre-update callback to update timestamp.
     * Automatically called by JPA before entity is updated.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Business method to calculate the subtotal price for this order item.
     * Subtotal = price × quantity.
     *
     * @return the subtotal price as BigDecimal
     */
    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Business method to get the display name for this order item.
     * Returns the product name or a default message if product is null.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return product != null ? product.getName() : "Unknown Product";
    }

    /**
     * Business method to get the product description.
     * Returns the product description or null if product is null.
     *
     * @return the product description
     */
    public String getProductDescription() {
        return product != null ? product.getDescription() : null;
    }

    /**
     * Business method to get the image URL for this order item.
     * Returns the product image URL or null if product is null.
     *
     * @return the image URL
     */
    public String getImageUrl() {
        return product != null ? product.getImageUrl() : null;
    }

    /**
     * Business method to check if this order item is valid.
     * Validates that the product exists and quantity is positive.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return product != null &&
               quantity != null &&
               quantity > 0 &&
               price != null &&
               price.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Business method to create an order item from a cart item.
     * This is a factory method to convert cart items to order items during checkout.
     *
     * @param cartItem the cart item to convert
     * @return a new OrderItem with the same product and quantity
     */
    public static OrderItem fromCartItem(CartItem cartItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(cartItem.getProduct());
        orderItem.setQuantity(cartItem.getQuantity());
        // Use current product price at the time of order
        orderItem.setPrice(cartItem.getProduct().getPrice());
        return orderItem;
    }

    /**
     * Business method to update the quantity of this order item.
     * This should only be used before the order is confirmed.
     *
     * @param newQuantity the new quantity
     * @throws IllegalArgumentException if newQuantity is not positive
     */
    public void updateQuantity(Integer newQuantity) {
        if (newQuantity == null || newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantity = newQuantity;
    }

    /**
     * Business method to update the price of this order item.
     * This should only be used before the order is confirmed.
     *
     * @param newPrice the new price
     * @throws IllegalArgumentException if newPrice is not positive
     */
    public void updatePrice(BigDecimal newPrice) {
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        this.price = newPrice;
    }

    /**
     * Business method to get a formatted string representation.
     * Useful for display purposes in order summaries.
     *
     * @return formatted string with product name, quantity, and price
     */
    @Override
    public String toString() {
        return String.format("%s x%d - $%.2f",
            getDisplayName(),
            quantity,
            getSubtotal());
    }
}
