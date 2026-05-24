package com.ecommerce.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Order entity representing a customer order in the e-commerce system.
 *
 * This entity follows the Single Responsibility Principle by being responsible
 * only for order-related data and relationships. It's a JPA entity that maps
 * to the 'orders' table in the database.
 *
 * The order represents a completed purchase transaction with multiple order items.
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"orderItems", "user"})
@ToString(exclude = {"orderItems", "user"})
public class Order {

    /**
     * Primary key for the order entity.
     * Uses GenerationType.IDENTITY for auto-increment in MySQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Many-to-one relationship with User entity.
     * Each order belongs to exactly one user.
     * Fetch type is LAZY to avoid loading user data when not needed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Date and time when the order was placed.
     * Automatically set on creation.
     */
    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    /**
     * Total price of the order including all items.
     * Should match the sum of all order item subtotals.
     */
    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.01", message = "Total price must be greater than 0")
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    /**
     * Current status of the order.
     * Defaults to PENDING when order is created.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    /**
     * Shipping address for the order.
     * Can be null for digital products or in-store pickup.
     */
    @Column(name = "shipping_address", length = 500)
    private String shippingAddress;

    /**
     * Billing address for the order.
     * Can be null if same as shipping address.
     */
    @Column(name = "billing_address", length = 500)
    private String billingAddress;

    /**
     * Tracking number for shipment.
     * Set when order is shipped.
     */
    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    /**
     * Notes or special instructions for the order.
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Timestamp when the order was created.
     * Automatically set on creation.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the order was last updated.
     * Automatically updated on any field change.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * One-to-many relationship with OrderItem entities.
     * An order can contain multiple order items.
     * Order items are deleted when order is deleted.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<OrderItem> orderItems = new HashSet<>();

    /**
     * Pre-persist callback to set creation timestamp and order date.
     * Automatically called by JPA before entity is saved.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
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
     * Business method to calculate the total price from order items.
     * Should be called before saving to ensure totalPrice is accurate.
     *
     * @return the calculated total price
     */
    public BigDecimal calculateTotalPrice() {
        return orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Business method to get the total number of items in the order.
     *
     * @return the total quantity of all items
     */
    public int getTotalItems() {
        return orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    /**
     * Business method to check if the order can be cancelled.
     * Orders can only be cancelled if they are in PENDING status.
     *
     * @return true if order can be cancelled, false otherwise
     */
    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING;
    }

    /**
     * Business method to check if the order can be shipped.
     * Orders can only be shipped if they are in PENDING status.
     *
     * @return true if order can be shipped, false otherwise
     */
    public boolean canBeShipped() {
        return status == OrderStatus.PENDING;
    }

    /**
     * Business method to check if the order can be delivered.
     * Orders can only be delivered if they are in SHIPPED status.
     *
     * @return true if order can be delivered, false otherwise
     */
    public boolean canBeDelivered() {
        return status == OrderStatus.SHIPPED;
    }

    /**
     * Business method to cancel the order.
     * Changes status to CANCELLED if order can be cancelled.
     *
     * @throws IllegalStateException if order cannot be cancelled
     */
    public void cancel() {
        if (!canBeCancelled()) {
            throw new IllegalStateException("Order cannot be cancelled in current status: " + status);
        }
        this.status = OrderStatus.CANCELLED;
    }

    /**
     * Business method to ship the order.
     * Changes status to SHIPPED and sets tracking number if provided.
     *
     * @param trackingNumber the tracking number (can be null)
     * @throws IllegalStateException if order cannot be shipped
     */
    public void ship(String trackingNumber) {
        if (!canBeShipped()) {
            throw new IllegalStateException("Order cannot be shipped in current status: " + status);
        }
        this.status = OrderStatus.SHIPPED;
        this.trackingNumber = trackingNumber;
    }

    /**
     * Business method to deliver the order.
     * Changes status to DELIVERED if order can be delivered.
     *
     * @throws IllegalStateException if order cannot be delivered
     */
    public void deliver() {
        if (!canBeDelivered()) {
            throw new IllegalStateException("Order cannot be delivered in current status: " + status);
        }
        this.status = OrderStatus.DELIVERED;
    }

    /**
     * Business method to add an order item.
     *
     * @param orderItem the order item to add
     */
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    /**
     * Business method to remove an order item.
     *
     * @param orderItem the order item to remove
     */
    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
    }

    /**
     * Enum representing possible order statuses.
     * Follows the Single Responsibility Principle by defining only status types.
     */
    public enum OrderStatus {
        PENDING,    // Order placed but not yet processed
        SHIPPED,    // Order has been shipped
        DELIVERED,  // Order has been delivered
        CANCELLED   // Order has been cancelled
    }
}
