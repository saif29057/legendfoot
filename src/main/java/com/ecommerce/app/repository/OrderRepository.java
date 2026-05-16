package com.ecommerce.app.repository;

import com.ecommerce.app.entity.Order;
import com.ecommerce.app.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Order entity operations.
 * 
 * This interface follows the Repository pattern and Single Responsibility Principle
 * by being responsible only for data access operations related to Order entities.
 * It extends JpaRepository to inherit standard CRUD operations and defines
 * custom query methods for specific order-related business requirements.
 * 
 * The interface provides methods for order management, supporting order
 * tracking, history, and administrative functions.
 * 
 * @Repository annotation indicates this is a Spring repository bean.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds orders by their associated user.
     * 
     * This method provides user-based order lookup, commonly used
     * to retrieve a user's order history.
     * 
     * @param user   user whose orders to find
     * @return List of orders belonging to the specified user
     */
    List<Order> findByUser(User user);

    /**
     * Finds orders by their associated user with pagination support.
     * 
     * This method provides paginated order history lookup,
     * suitable for order history pages with large datasets.
     * 
     * @param user   user whose orders to find
     * @param pageable pagination information
     * @return Page of orders belonging to the specified user
     */
    Page<Order> findByUser(User user, Pageable pageable);

    /**
     * Finds orders by user username with pagination support.
     * 
     * This method provides paginated order history lookup by username,
     * useful when User entity is not directly available.
     * 
     * @param username username whose orders to find
     * @param pageable pagination information
     * @return Page of orders belonging to the specified username
     */
    @Query("SELECT o FROM Order o WHERE o.user.username = :username")
    Page<Order> findByUserUsername(@Param("username") String username, Pageable pageable);

    /**
     * Finds orders by their status.
     * 
     * This method is useful for administrative purposes to filter
     * orders by their current processing status.
     * 
     * @param status order status to filter by
     * @return List of orders with the specified status
     */
    List<Order> findByStatus(Order.OrderStatus status);

    /**
     * Finds orders by status with pagination support.
     * 
     * This method provides paginated status-based filtering,
     * suitable for administrative dashboards.
     * 
     * @param status  order status to filter by
     * @param pageable pagination information
     * @return Page of orders with the specified status
     */
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

    /**
     * Finds orders by user and status.
     * 
     * This method combines user and status filtering, useful for
     * finding specific types of orders for a user.
     * 
     * @param user   user whose orders to find
     * @param status order status to filter by
     * @return List of orders belonging to user with specified status
     */
    @Query("SELECT o FROM Order o WHERE o.user = :user AND o.status = :status")
    List<Order> findByUserAndStatus(@Param("user") User user, @Param("status") Order.OrderStatus status);

    /**
     * Finds orders placed within a specific date range.
     * 
     * This method is useful for generating reports and analytics
     * for specific time periods.
     * 
     * @param startDate start of date range (inclusive)
     * @param endDate   end of date range (inclusive)
     * @return List of orders placed within the date range
     */
    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<Order> findByOrderDateBetween(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    /**
     * Finds orders placed within a specific date range with pagination support.
     * 
     * This method provides paginated date-range filtering,
     * suitable for report generation with large datasets.
     * 
     * @param startDate start of date range (inclusive)
     * @param endDate   end of date range (inclusive)
     * @param pageable pagination information
     * @return Page of orders placed within the date range
     */
    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    Page<Order> findByOrderDateBetween(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate,
                                     Pageable pageable);

    /**
     * Finds recent orders.
     * 
     * This method is useful for displaying recent orders on
     * dashboards and homepages.
     * 
     * @param pageable pagination information
     * @return Page of recent orders sorted by order date (newest first)
     */
    @Query("SELECT o FROM Order o ORDER BY o.orderDate DESC")
    Page<Order> findRecentOrders(Pageable pageable);

    /**
     * Finds recent orders for a specific user.
     * 
     * This method is useful for displaying a user's recent
     * order activity.
     * 
     * @param user   user whose recent orders to find
     * @param pageable pagination information
     * @return Page of recent orders for the user
     */
    @Query("SELECT o FROM Order o WHERE o.user = :user ORDER BY o.orderDate DESC")
    Page<Order> findRecentOrdersByUser(@Param("user") User user, Pageable pageable);

    /**
     * Finds orders with total price greater than specified amount.
     * 
     * This method is useful for identifying high-value orders
     * for analytics and customer segmentation.
     * 
     * @param minPrice minimum total price
     * @return List of orders with total price >= minPrice
     */
    @Query("SELECT o FROM Order o WHERE o.totalPrice >= :minPrice ORDER BY o.totalPrice DESC")
    List<Order> findOrdersWithMinPrice(@Param("minPrice") java.math.BigDecimal minPrice);

    /**
     * Finds orders with total price within a specific range.
     * 
     * This method is useful for filtering orders by price range
     * for analytical purposes.
     * 
     * @param minPrice minimum total price
     * @param maxPrice maximum total price
     * @return List of orders within the price range
     */
    @Query("SELECT o FROM Order o WHERE o.totalPrice BETWEEN :minPrice AND :maxPrice ORDER BY o.totalPrice DESC")
    List<Order> findOrdersByPriceRange(@Param("minPrice") java.math.BigDecimal minPrice,
                                      @Param("maxPrice") java.math.BigDecimal maxPrice);

    /**
     * Finds orders that can be cancelled (PENDING status).
     * 
     * This method is useful for identifying orders that are
     * still eligible for cancellation.
     * 
     * @return List of cancellable orders
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING'")
    List<Order> findCancellableOrders();

    /**
     * Finds orders that can be shipped (PENDING status).
     * 
     * This method is useful for identifying orders that are
     * ready to be shipped.
     * 
     * @return List of shippable orders
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING'")
    List<Order> findShippableOrders();

    /**
     * Finds orders that can be delivered (SHIPPED status).
     * 
     * This method is useful for identifying orders that are
     * in transit and can be marked as delivered.
     * 
     * @return List of deliverable orders
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'SHIPPED'")
    List<Order> findDeliverableOrders();

    /**
     * Counts orders by status.
     * 
     * This method provides statistical information about
     * order distribution across different statuses.
     * 
     * @param status order status to count
     * @return number of orders with the specified status
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") Order.OrderStatus status);

    /**
     * Counts orders for a specific user.
     * 
     * This method provides statistical information about
     * a user's order history.
     * 
     * @param user   user whose orders to count
     * @return number of orders for the user
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user = :user")
    long countByUser(@Param("user") User user);

    /**
     * Counts orders placed within a specific date range.
     * 
     * This method provides statistical information for
     * reporting and analytics.
     * 
     * @param startDate start of date range (inclusive)
     * @param endDate   end of date range (inclusive)
     * @return number of orders placed within the date range
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    long countByOrderDateBetween(@Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate);

    /**
     * Calculates total revenue within a specific date range.
     * 
     * This method provides financial statistics by summing
     * the total price of all orders in the date range.
     * 
     * @param startDate start of date range (inclusive)
     * @param endDate   end of date range (inclusive)
     * @return total revenue for the date range
     */
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal calculateRevenueBetween(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    /**
     * Calculates total revenue from orders with specific status.
     * 
     * This method provides financial statistics by summing
     * the total price of orders with a specific status.
     * 
     * @param status order status to filter by
     * @return total revenue for orders with the specified status
     */
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.status = :status")
    java.math.BigDecimal calculateRevenueByStatus(@Param("status") Order.OrderStatus status);

    /**
     * Finds orders with tracking numbers.
     * 
     * This method is useful for identifying orders that
     * have been shipped and have tracking information.
     * 
     * @return List of orders with tracking numbers
     */
    @Query("SELECT o FROM Order o WHERE o.trackingNumber IS NOT NULL ORDER BY o.updatedAt DESC")
    List<Order> findOrdersWithTrackingNumbers();

    /**
     * Finds orders without tracking numbers.
     * 
     * This method is useful for identifying orders that
     * need tracking numbers to be assigned.
     * 
     * @return List of orders without tracking numbers
     */
    @Query("SELECT o FROM Order o WHERE o.trackingNumber IS NULL AND o.status = 'SHIPPED'")
    List<Order> findShippedOrdersWithoutTrackingNumbers();
}
