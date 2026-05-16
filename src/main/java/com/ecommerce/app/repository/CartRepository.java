package com.ecommerce.app.repository;

import com.ecommerce.app.entity.Cart;
import com.ecommerce.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Cart entity operations.
 * 
 * This interface follows the Repository pattern and Single Responsibility Principle
 * by being responsible only for data access operations related to Cart entities.
 * It extends JpaRepository to inherit standard CRUD operations and defines
 * custom query methods for specific cart-related business requirements.
 * 
 * The interface provides methods for cart management, supporting shopping
 * cart functionality in the e-commerce system.
 * 
 * @Repository annotation indicates this is a Spring repository bean.
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Finds a cart by its associated user.
     * 
     * This method provides user-based cart lookup, which is commonly
     * used to retrieve a user's current shopping cart.
     * 
     * @param user the user whose cart to find
     * @return Optional containing the cart if found, empty otherwise
     */
    Optional<Cart> findByUser(User user);

    /**
     * Finds an active cart for a specific user.
     * 
     * This method is specifically designed to find the user's current
     * active shopping cart, which is the one they are currently using.
     * 
     * @param user the user whose active cart to find
     * @return Optional containing the active cart if found, empty otherwise
     */
    @Query("SELECT c FROM Cart c WHERE c.user = :user AND c.active = true")
    Optional<Cart> findActiveCartByUser(@Param("user") User user);

    /**
     * Finds any cart (active or inactive) for a specific user.
     * 
     * This method provides a comprehensive lookup that returns any cart
     * associated with the user, regardless of active status.
     * 
     * @param user the user whose cart to find
     * @return Optional containing any cart if found, empty otherwise
     */
    @Query("SELECT c FROM Cart c WHERE c.user = :user")
    Optional<Cart> findAnyCartByUser(@Param("user") User user);

    /**
     * Checks if a user has an active cart.
     * 
     * This method is useful for determining whether to create a new cart
     * or use an existing one during shopping operations.
     * 
     * @param user the user to check
     * @return true if user has an active cart, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cart c WHERE c.user = :user AND c.active = true")
    boolean hasActiveCart(@Param("user") User user);

    /**
     * Counts the number of carts for a specific user.
     * 
     * This method provides statistical information about a user's
     * cart history, useful for analytics and user management.
     * 
     * @param user the user whose carts to count
     * @return the number of carts associated with the user
     */
    @Query("SELECT COUNT(c) FROM Cart c WHERE c.user = :user")
    long countCartsByUser(@Param("user") User user);

    /**
     * Counts the number of active carts for a specific user.
     * 
     * This method is useful for validation to ensure users don't
     * have multiple active carts simultaneously.
     * 
     * @param user the user whose active carts to count
     * @return the number of active carts associated with the user
     */
    @Query("SELECT COUNT(c) FROM Cart c WHERE c.user = :user AND c.active = true")
    long countActiveCartsByUser(@Param("user") User user);

    /**
     * Finds all carts for a specific user.
     * 
     * This method provides a complete list of all carts associated
     * with a user, including both active and inactive carts.
     * 
     * @param user the user whose carts to find
     * @return Iterable of all carts for the user
     */
    @Query("SELECT c FROM Cart c WHERE c.user = :user ORDER BY c.createdAt DESC")
    Iterable<Cart> findAllCartsByUser(@Param("user") User user);

    /**
     * Finds all active carts in the system.
     * 
     * This method is useful for administrative purposes to see
     * all currently active shopping carts across all users.
     * 
     * @return Iterable of all active carts
     */
    @Query("SELECT c FROM Cart c WHERE c.active = true ORDER BY c.updatedAt DESC")
    Iterable<Cart> findAllActiveCarts();

    /**
     * Finds all inactive carts in the system.
     * 
     * This method is useful for cleanup operations and analytics
     * to identify abandoned or completed carts.
     * 
     * @return Iterable of all inactive carts
     */
    @Query("SELECT c FROM Cart c WHERE c.active = false ORDER BY c.updatedAt DESC")
    Iterable<Cart> findAllInactiveCarts();

    /**
     * Finds carts that have been updated within a specific time range.
     * 
     * This method is useful for analytics and identifying recently
     * active shopping carts.
     * 
     * @param startTime start of time range
     * @param endTime end of time range
     * @return Iterable of carts updated within the time range
     */
    @Query("SELECT c FROM Cart c WHERE c.updatedAt BETWEEN :startTime AND :endTime ORDER BY c.updatedAt DESC")
    Iterable<Cart> findCartsUpdatedBetween(@Param("startTime") java.time.LocalDateTime startTime,
                                          @Param("endTime") java.time.LocalDateTime endTime);

    /**
     * Finds active carts that have been updated within a specific time range.
     * 
     * This method combines active status filtering with time-based filtering
     * to identify recently active shopping carts.
     * 
     * @param startTime start of time range
     * @param endTime end of time range
     * @return Iterable of active carts updated within the time range
     */
    @Query("SELECT c FROM Cart c WHERE c.active = true AND c.updatedAt BETWEEN :startTime AND :endTime ORDER BY c.updatedAt DESC")
    Iterable<Cart> findActiveCartsUpdatedBetween(@Param("startTime") java.time.LocalDateTime startTime,
                                              @Param("endTime") java.time.LocalDateTime endTime);

    /**
     * Counts total number of active carts in the system.
     * 
     * This method provides statistical information about the current
     * shopping activity across all users.
     * 
     * @return the total number of active carts
     */
    @Query("SELECT COUNT(c) FROM Cart c WHERE c.active = true")
    long countActiveCarts();

    /**
     * Counts total number of inactive carts in the system.
     * 
     * This method provides statistical information about completed
     * or abandoned shopping carts.
     * 
     * @return the total number of inactive carts
     */
    @Query("SELECT COUNT(c) FROM Cart c WHERE c.active = false")
    long countInactiveCarts();
}
