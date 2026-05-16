package com.ecommerce.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Cart entity representing a shopping cart in the e-commerce system.
 * 
 * This entity follows the Single Responsibility Principle by being responsible
 * only for cart-related data and relationships. It's a JPA entity that maps
 * to the 'carts' table in the database.
 * 
 * The cart represents a user's shopping session and contains multiple cart items.
 * Each user can have multiple carts (e.g., active cart, saved carts).
 */
@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    /**
     * Primary key for the cart entity.
     * Uses GenerationType.IDENTITY for auto-increment in MySQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Many-to-one relationship with User entity.
     * Each cart belongs to exactly one user.
     * Fetch type is LAZY to avoid loading user data when not needed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Flag indicating if this is the user's active cart.
     * Users typically have one active cart at a time.
     */
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    /**
     * Timestamp when the cart was created.
     * Automatically set on creation.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the cart was last updated.
     * Automatically updated on any field change.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * One-to-many relationship with CartItem entities.
     * A cart can contain multiple cart items.
     * Cart items are deleted when cart is deleted.
     */
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CartItem> cartItems = new HashSet<>();

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
     * Business method to calculate the total price of all items in the cart.
     * 
     * @return the total price as BigDecimal, or BigDecimal.ZERO if cart is empty
     */
    public BigDecimal getTotalPrice() {
        return cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Business method to get the total number of items in the cart.
     * 
     * @return the total quantity of all items, or 0 if cart is empty
     */
    public int getTotalItems() {
        return cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    /**
     * Business method to check if the cart is empty.
     * 
     * @return true if cart has no items, false otherwise
     */
    public boolean isEmpty() {
        return cartItems == null || cartItems.isEmpty();
    }

    /**
     * Business method to add a product to the cart.
     * If the product already exists in the cart, increases the quantity.
     * 
     * @param product the product to add
     * @param quantity the quantity to add
     * @throws IllegalArgumentException if quantity is not positive
     */
    public void addProduct(Product product, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        // Check if product already exists in cart
        CartItem existingItem = findCartItemByProduct(product);
        
        if (existingItem != null) {
            // Update existing item quantity
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // Create new cart item
            CartItem cartItem = new CartItem();
            cartItem.setCart(this);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItems.add(cartItem);
        }
    }

    /**
     * Business method to remove a product from the cart.
     * 
     * @param product the product to remove
     * @return true if product was removed, false if not found
     */
    public boolean removeProduct(Product product) {
        CartItem cartItem = findCartItemByProduct(product);
        if (cartItem != null) {
            cartItems.remove(cartItem);
            return true;
        }
        return false;
    }

    /**
     * Business method to update the quantity of a product in the cart.
     * 
     * @param product the product to update
     * @param quantity the new quantity
     * @throws IllegalArgumentException if quantity is not positive
     */
    public void updateProductQuantity(Product product, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        CartItem cartItem = findCartItemByProduct(product);
        if (cartItem != null) {
            cartItem.setQuantity(quantity);
        } else {
            throw new IllegalArgumentException("Product not found in cart");
        }
    }

    /**
     * Helper method to find a cart item by product.
     * 
     * @param product the product to find
     * @return the CartItem if found, null otherwise
     */
    private CartItem findCartItemByProduct(Product product) {
        return cartItems.stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Business method to clear all items from the cart.
     * Used after checkout or when user wants to start over.
     */
    public void clear() {
        cartItems.clear();
    }
}
