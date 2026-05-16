package com.ecommerce.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * User entity representing a user in the e-commerce system.
 * 
 * This entity follows the Single Responsibility Principle by being responsible
 * only for user-related data and relationships. It's a JPA entity that maps
 * to the 'users' table in the database.
 * 
 * The entity uses Lombok annotations to reduce boilerplate code and follows
 * Java Bean conventions with proper validation annotations.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class User {

    /**
     * Primary key for the user entity.
     * Uses GenerationType.IDENTITY for auto-increment in MySQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Username for authentication and display purposes.
     * Must be unique and between 3-50 characters.
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    /**
     * Email address for user communication and password recovery.
     * Must be unique and valid email format.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    /**
     * Encrypted password for authentication.
     * Should be encrypted using BCrypt before storing.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * User role for authorization (USER or ADMIN).
     * Defaults to USER role for new registrations.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.USER;

    /**
     * Flag indicating if the user account is enabled.
     * Used for soft deletion and account suspension.
     */
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    /**
     * Checks if the user account is enabled.
     * 
     * @return true if user is enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled != null && enabled;
    }

    /**
     * Timestamp when the user account was created.
     * Automatically set on creation.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the user account was last updated.
     * Automatically updated on any field change.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * One-to-many relationship with Cart entities.
     * A user can have multiple carts (though typically one active cart).
     * Cascade operations ensure cart is deleted when user is deleted.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Cart> carts = new HashSet<>();

    /**
     * One-to-many relationship with Order entities.
     * A user can have multiple orders.
     * Orders are not deleted when user is deleted (business rule).
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Order> orders = new HashSet<>();

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
     * Enum representing user roles in the system.
     * Follows the Single Responsibility Principle by defining only role types.
     */
    public enum Role {
        USER,    // Regular customer role
        ADMIN    // Administrator role with full access
    }
}
