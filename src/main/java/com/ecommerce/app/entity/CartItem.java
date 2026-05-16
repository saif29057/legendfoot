package com.ecommerce.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * CartItem entity representing an item in a shopping cart.
 * 
 * This entity follows the Single Responsibility Principle by being responsible
 * only for cart item data and relationships. It's a JPA entity that maps
 * to the 'cart_items' table in the database.
 * 
 * The cart item represents a product with a specific quantity in a user's cart.
 */
@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    /**
     * Primary key for the cart item entity.
     * Uses GenerationType.IDENTITY for auto-increment in MySQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Many-to-one relationship with Cart entity.
     * Each cart item belongs to exactly one cart.
     * Fetch type is LAZY to avoid loading cart data when not needed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    /**
     * Many-to-one relationship with Product entity.
     * Each cart item references exactly one product.
     * Fetch type is LAZY to avoid loading product data when not needed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Quantity of the product in the cart.
     * Must be positive and should not exceed available stock.
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * Timestamp when the cart item was created.
     * Automatically set on creation.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the cart item was last updated.
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
     * Business method to calculate the subtotal price for this cart item.
     * Subtotal = product price × quantity.
     * 
     * @return the subtotal price as BigDecimal
     * @throws IllegalStateException if product is null
     */
    public BigDecimal getSubtotal() {
        if (product == null) {
            throw new IllegalStateException("Product cannot be null");
        }
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Business method to increase the quantity of this cart item.
     * 
     * @param additionalQuantity the quantity to add
     * @throws IllegalArgumentException if additionalQuantity is not positive
     */
    public void increaseQuantity(Integer additionalQuantity) {
        if (additionalQuantity == null || additionalQuantity <= 0) {
            throw new IllegalArgumentException("Additional quantity must be positive");
        }
        this.quantity += additionalQuantity;
    }

    /**
     * Business method to decrease the quantity of this cart item.
     * Ensures quantity doesn't go below 1.
     * 
     * @param reductionQuantity the quantity to reduce
     * @throws IllegalArgumentException if reductionQuantity is not positive
     * @throws IllegalStateException if reduction would make quantity less than 1
     */
    public void decreaseQuantity(Integer reductionQuantity) {
        if (reductionQuantity == null || reductionQuantity <= 0) {
            throw new IllegalArgumentException("Reduction quantity must be positive");
        }
        if (this.quantity - reductionQuantity < 1) {
            throw new IllegalStateException("Quantity cannot be less than 1");
        }
        this.quantity -= reductionQuantity;
    }

    /**
     * Business method to check if this cart item is valid for checkout.
     * Validates that the product exists and has sufficient stock.
     * 
     * @return true if valid for checkout, false otherwise
     */
    public boolean isValidForCheckout() {
        return product != null && 
               product.isActive() && 
               product.isInStock() && 
               product.getStockQuantity() >= quantity;
    }

    /**
     * Business method to get the display name for this cart item.
     * Returns the product name or a default message if product is null.
     * 
     * @return the display name
     */
    public String getDisplayName() {
        return product != null ? product.getName() : "Unknown Product";
    }

    /**
     * Business method to get the display price for this cart item.
     * Returns the product price or BigDecimal.ZERO if product is null.
     * 
     * @return the display price
     */
    public BigDecimal getDisplayPrice() {
        return product != null ? product.getPrice() : BigDecimal.ZERO;
    }

    /**
     * Business method to get the image URL for this cart item.
     * Returns the product image URL or null if product is null.
     * 
     * @return the image URL
     */
    public String getImageUrl() {
        return product != null ? product.getImageUrl() : null;
    }
}
