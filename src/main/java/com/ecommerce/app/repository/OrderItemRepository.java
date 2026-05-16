package com.ecommerce.app.repository;

import com.ecommerce.app.entity.OrderItem;
import com.ecommerce.app.entity.Order;
import com.ecommerce.app.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for OrderItem entity operations.
 * 
 * This interface follows the Repository pattern and Single Responsibility Principle
 * by being responsible only for data access operations related to OrderItem entities.
 * It extends JpaRepository to inherit standard CRUD operations and defines
 * custom query methods for specific order item-related business requirements.
 * 
 * The interface provides methods for order item management, supporting order
 * tracking, analytics, and inventory management.
 * 
 * @Repository annotation indicates this is a Spring repository bean.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Finds order items by their associated order.
     * 
     * This method provides order-based item lookup, commonly used to
     * retrieve all items in a specific order.
     * 
     * @param order  order whose items to find
     * @return List of order items belonging to the specified order
     */
    List<OrderItem> findByOrder(Order order);

    /**
     * Finds order items by their associated order ID.
     * 
     * This method provides order-based item lookup using order ID,
     * commonly used to retrieve all items in a specific order.
     * 
     * @param orderId ID of order whose items to find
     * @return List of order items belonging to the specified order
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);

    /**
     * Finds order items by their associated product.
     * 
     * This method provides product-based item lookup, useful for
     * tracking which orders contain a specific product.
     * 
     * @param product product whose order items to find
     * @return List of order items containing the specified product
     */
    List<OrderItem> findByProduct(Product product);

    /**
     * Finds order items by order and product.
     * 
     * This method is useful for verifying if a specific product
     * is included in a particular order.
     * 
     * @param order  order to search in
     * @param product product to search for
     * @return List of order items matching both order and product
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order = :order AND oi.product = :product")
    List<OrderItem> findByOrderAndProduct(@Param("order") Order order, @Param("product") Product product);

    /**
     * Counts the number of items in a specific order.
     * 
     * This method provides statistical information about
     * order size and complexity.
     * 
     * @param order  order whose items to count
     * @return number of items in the order
     */
    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.order = :order")
    long countByOrder(@Param("order") Order order);

    /**
     * Counts the total quantity of all items in a specific order.
     * 
     * This method calculates the sum of quantities of all items
     * in an order, providing the total number of products.
     * 
     * @param order  order whose total quantity to calculate
     * @return total quantity of all items in the order
     */
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.order = :order")
    Long getTotalQuantityByOrder(@Param("order") Order order);

    /**
     * Calculates the total price of all items in a specific order.
     * 
     * This method computes the order total by summing
     * (price × quantity) for all items in the order.
     * 
     * @param order  order whose total price to calculate
     * @return total price of all items in the order
     */
    @Query("SELECT SUM(oi.price * oi.quantity) FROM OrderItem oi WHERE oi.order = :order")
    BigDecimal getTotalPriceByOrder(@Param("order") Order order);

    /**
     * Finds order items with quantity greater than specified minimum.
     * 
     * This method is useful for analytics to identify
     * bulk purchases or popular products.
     * 
     * @param minQuantity minimum quantity threshold
     * @return List of order items with quantity >= minQuantity
     */
    List<OrderItem> findByQuantityGreaterThanEqual(Integer minQuantity);

    /**
     * Finds order items with price greater than specified minimum.
     * 
     * This method is useful for analytics to identify
     * high-value items in orders.
     * 
     * @param minPrice minimum price threshold
     * @return List of order items with price >= minPrice
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.price >= :minPrice")
    List<OrderItem> findByPriceGreaterThanEqual(@Param("minPrice") BigDecimal minPrice);

    /**
     * Finds order items within a price range.
     * 
     * This method is useful for filtering items by price
     * for analytical purposes.
     * 
     * @param minPrice minimum price (inclusive)
     * @param maxPrice maximum price (inclusive)
     * @return List of order items within the price range
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.price BETWEEN :minPrice AND :maxPrice")
    List<OrderItem> findByPriceBetween(@Param("minPrice") BigDecimal minPrice, 
                                    @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Finds order items for a specific product with pagination support.
     * 
     * This method provides paginated results for products
     * that appear in many orders.
     * 
     * @param product product whose order items to find
     * @param pageable pagination information
     * @return Page of order items for the specified product
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.product = :product ORDER BY oi.createdAt DESC")
    org.springframework.data.domain.Page<OrderItem> findByProduct(Product product, 
                                                         org.springframework.data.domain.Pageable pageable);

    /**
     * Finds the most frequently ordered products.
     * 
     * This method is useful for analytics and inventory planning
     * to identify popular products.
     * 
     * @param limit maximum number of results to return
     * @return List of products ordered by frequency (most frequent first)
     */
    @Query(value = "SELECT p.* FROM products p " +
                   "JOIN order_items oi ON p.id = oi.product_id " +
                   "GROUP BY p.id " +
                   "ORDER BY SUM(oi.quantity) DESC " +
                   "LIMIT :limit", 
           nativeQuery = true)
    List<Product> findMostFrequentlyOrderedProducts(@Param("limit") Integer limit);

    /**
     * Finds the highest revenue generating products.
     * 
     * This method is useful for analytics to identify
     * products that generate the most revenue.
     * 
     * @param limit maximum number of results to return
     * @return List of products ordered by revenue (highest first)
     */
    @Query(value = "SELECT p.* FROM products p " +
                   "JOIN order_items oi ON p.id = oi.product_id " +
                   "GROUP BY p.id " +
                   "ORDER BY SUM(oi.price * oi.quantity) DESC " +
                   "LIMIT :limit", 
           nativeQuery = true)
    List<Product> findHighestRevenueProducts(@Param("limit") Integer limit);

    /**
     * Calculates total quantity sold for a specific product.
     * 
     * This method provides sales statistics for individual
     * products, useful for inventory and marketing.
     * 
     * @param product product whose total sales to calculate
     * @return total quantity sold for the product
     */
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.product = :product")
    Long getTotalQuantitySoldByProduct(@Param("product") Product product);

    /**
     * Calculates total revenue generated by a specific product.
     * 
     * This method provides revenue statistics for individual
     * products, useful for business analysis.
     * 
     * @param product product whose total revenue to calculate
     * @return total revenue generated by the product
     */
    @Query("SELECT SUM(oi.price * oi.quantity) FROM OrderItem oi WHERE oi.product = :product")
    BigDecimal getTotalRevenueByProduct(@Param("product") Product product);

    /**
     * Finds order items created within a specific date range.
     * 
     * This method is useful for time-based analytics and
     * reporting on order item activity.
     * 
     * @param startDate start of date range (inclusive)
     * @param endDate   end of date range (inclusive)
     * @return List of order items created within the date range
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.createdAt BETWEEN :startDate AND :endDate")
    List<OrderItem> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate,
                                        @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * Deletes all order items for a specific order.
     * 
     * This method is useful for order cleanup operations
     * when an order is deleted or needs to be reset.
     * 
     * @param order  order whose items to delete
     * @return number of deleted items
     */
    long deleteByOrder(Order order);

    /**
     * Deletes all order items for a specific product.
     * 
     * This method is useful for product cleanup operations
     * when a product is deleted from the system.
     * 
     * @param product product whose order items to delete
     * @return number of deleted items
     */
    long deleteByProduct(Product product);

    /**
     * Finds order items sorted by creation date (newest first).
     * 
     * This method is useful for displaying recent order items
     * in administrative dashboards.
     * 
     * @return List of all order items sorted by creation date
     */
    @Query("SELECT oi FROM OrderItem oi ORDER BY oi.createdAt DESC")
    List<OrderItem> findAllOrderItemsByCreatedAtDesc();

    /**
     * Calculates the average order value.
     * 
     * This method provides statistical information about
     * average order value across all orders.
     * 
     * @return average order value
     */
    @Query("SELECT AVG(oi.price * oi.quantity) FROM OrderItem oi")
    BigDecimal calculateAverageOrderItemValue();

    /**
     * Counts total number of order items in the system.
     * 
     * This method provides statistical information about
     * overall system activity.
     * 
     * @return total number of order items
     */
    @Query("SELECT COUNT(oi) FROM OrderItem oi")
    long countAllOrderItems();

    /**
     * Sums total quantity of all products sold.
     * 
     * This method provides statistical information about
     * total sales volume across all products.
     * 
     * @return total quantity of all products sold
     */
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi")
    Long getTotalQuantitySold();
}
