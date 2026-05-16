package com.ecommerce.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Product entity representing a product in the e-commerce system.
 * 
 * This entity follows the Single Responsibility Principle by being responsible
 * only for product-related data and relationships. It's a JPA entity that maps
 * to the 'products' table in the database.
 * 
 * The entity uses validation annotations to ensure data integrity and follows
 * Java Bean conventions with proper field mappings.
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /**
     * Primary key for the product entity.
     * Uses GenerationType.IDENTITY for auto-increment in MySQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Product name for display and search purposes.
     * Must be between 2-100 characters.
     */
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * Detailed description of the product.
     * Can be null for simple products.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Current price of the product.
     * Must be positive and uses BigDecimal for monetary precision.
     */
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Current stock quantity available for sale.
     * Cannot be negative.
     */
    @NotNull(message = "Stock quantity is required")
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    /**
     * URL or path to the product image.
     * Can be null if no image is available.
     */
    @Column(name = "image_url", length = 255)
    private String imageUrl;

    /**
     * Flag indicating if the product is active and available for purchase.
     * Used for soft deletion and product management.
     */
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    /**
     * Timestamp when the product was created.
     * Automatically set on creation.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the product was last updated.
     * Automatically updated on any field change.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * One-to-many relationship with CartItem entities.
     * A product can appear in multiple cart items across different carts.
     * Cart items are deleted when product is deleted.
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CartItem> cartItems = new HashSet<>();

    /**
     * One-to-many relationship with OrderItem entities.
     * A product can appear in multiple order items across different orders.
     * Order items are preserved for historical records when product is deleted.
     */
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private Set<OrderItem> orderItems = new HashSet<>();

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
     * Business method to check if product is in stock.
     * 
     * @return true if stock quantity is greater than 0 and product is active
     */
    public boolean isInStock() {
        return active && stockQuantity != null && stockQuantity > 0;
    }

    /**
     * Checks if the product is active.
     * 
     * @return true if product is active, false otherwise
     */
    public boolean isActive() {
        return active != null && active;
    }

    /**
     * Business method to reduce stock quantity.
     * Ensures stock doesn't go negative.
     * 
     * @param quantity the amount to reduce from stock
     * @throws IllegalArgumentException if quantity is negative or exceeds available stock
     */
    public void reduceStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (stockQuantity < quantity) {
            throw new IllegalArgumentException("Insufficient stock available");
        }
        this.stockQuantity -= quantity;
    }

    /**
     * Business method to increase stock quantity.
     * Used for restocking products.
     * 
     * @param quantity the amount to add to stock
     * @throws IllegalArgumentException if quantity is negative
     */
    public void increaseStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.stockQuantity += quantity;
    }
}
