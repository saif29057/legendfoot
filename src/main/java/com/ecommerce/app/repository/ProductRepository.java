package com.ecommerce.app.repository;

import com.ecommerce.app.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product entity operations.
 * 
 * This interface follows the Repository pattern and Single Responsibility Principle
 * by being responsible only for data access operations related to Product entities.
 * It extends JpaRepository to inherit standard CRUD operations and defines
 * custom query methods for specific product-related business requirements.
 * 
 * The interface provides methods for product search, filtering, and inventory
 * management, supporting various e-commerce functionalities.
 * 
 * @Repository annotation indicates this is a Spring repository bean.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Finds a product by its name.
     * 
     * This method provides exact name matching for product lookup,
     * useful for inventory management and product verification.
     * 
     * @param name the exact product name to search for
     * @return Optional containing the product if found, empty otherwise
     */
    Optional<Product> findByName(String name);

    /**
     * Checks if a product exists with the given name.
     * 
     * This method is useful for validation during product creation
     * to ensure name uniqueness.
     * 
     * @param name the product name to check
     * @return true if a product with this name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Finds products whose name contains the given string (case-insensitive).
     * 
     * This method supports the search functionality where users can find
     * products by partial name matches, enhancing user experience.
     * 
     * @param name the name fragment to search for
     * @return List of products with matching names
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Finds products whose name or description contains the given string (case-insensitive).
     * 
     * This method provides comprehensive search functionality by searching
     * both product names and descriptions, improving search relevance.
     * 
     * @param keyword the keyword to search for
     * @return List of products with matching names or descriptions
     */
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> findByNameOrDescriptionContainingIgnoreCase(@Param("keyword") String keyword);

    /**
     * Finds products within a price range.
     * 
     * This method supports price-based filtering, allowing users to
     * find products within their budget constraints.
     * 
     * @param minPrice the minimum price (inclusive)
     * @param maxPrice the maximum price (inclusive)
     * @return List of products within the price range
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceBetween(@Param("minPrice") BigDecimal minPrice, 
                                   @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Finds products with price less than or equal to the given amount.
     * 
     * This method is useful for budget-conscious users looking for
     * affordable products.
     * 
     * @param maxPrice the maximum price
     * @return List of products with price <= maxPrice
     */
    List<Product> findByPriceLessThanEqual(BigDecimal maxPrice);

    /**
     * Finds products with price less than or equal to the given amount with pagination.
     * 
     * This method is useful for budget-conscious users looking for
     * affordable products with pagination support.
     * 
     * @param maxPrice the maximum price
     * @param pageable pagination information
     * @return Page of products with price <= maxPrice
     */
    Page<Product> findByPriceLessThanEqual(BigDecimal maxPrice, Pageable pageable);

    /**
     * Finds products with price greater than or equal to the given amount.
     * 
     * This method is useful for users looking for premium products
     * or filtering out low-cost items.
     * 
     * @param minPrice the minimum price
     * @return List of products with price >= minPrice
     */
    List<Product> findByPriceGreaterThanEqual(BigDecimal minPrice);

    /**
     * Finds products with price greater than or equal to the given amount with pagination.
     * 
     * This method is useful for users looking for premium products
     * or filtering out low-cost items with pagination support.
     * 
     * @param minPrice the minimum price
     * @param pageable pagination information
     * @return Page of products with price >= minPrice
     */
    Page<Product> findByPriceGreaterThanEqual(BigDecimal minPrice, Pageable pageable);

    /**
     * Finds products that are currently in stock.
     * 
     * This method filters for available products, excluding out-of-stock items.
     * 
     * @return List of products with stock quantity > 0 and active status
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0 AND p.active = true")
    List<Product> findInStockProducts();

    /**
     * Finds products that are out of stock.
     * 
     * This method is useful for inventory management to identify
     * products that need restocking.
     * 
     * @return List of products with stock quantity <= 0
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= 0")
    List<Product> findOutOfStockProducts();

    /**
     * Finds products with low stock (quantity less than specified threshold).
     * 
     * This method is useful for inventory management to identify
     * products that need to be reordered soon.
     * 
     * @param threshold the stock quantity threshold
     * @return List of products with stock quantity < threshold
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity < :threshold AND p.active = true")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);

    /**
     * Finds active products only.
     * 
     * This method filters for products that are currently available
     * for purchase, excluding deactivated products.
     * 
     * @return List of active products
     */
    List<Product> findByActiveTrue();

    /**
     * Finds inactive products only.
     * 
     * This method is useful for administrative purposes to manage
     * deactivated products.
     * 
     * @return List of inactive products
     */
    List<Product> findByActiveFalse();

    /**
     * Searches products with pagination support.
     * 
     * This method provides paginated search results for better performance
     * and user experience when dealing with large product catalogs.
     * 
     * @param keyword the search keyword
     * @param pageable pagination information
     * @return Page of matching products
     */
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Gets all active products with pagination.
     * 
     * This method provides paginated results of active products,
     * suitable for product listing pages.
     * 
     * @param pageable pagination information
     * @return Page of active products
     */
    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.createdAt DESC")
    Page<Product> findActiveProducts(Pageable pageable);

    /**
     * Counts active products.
     * 
     * This method provides statistical information about the number
     * of products currently available for purchase.
     * 
     * @return the number of active products
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.active = true")
    long countActiveProducts();

    /**
     * Counts out of stock products.
     * 
     * This method provides statistical information about inventory
     * status for administrative dashboards.
     * 
     * @return the number of out of stock products
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity <= 0")
    long countOutOfStockProducts();

    /**
     * Finds products sorted by price (ascending).
     * 
     * This method supports price-based sorting for product listings.
     * 
     * @return List of products sorted by price (low to high)
     */
    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.price ASC")
    List<Product> findActiveProductsOrderByPriceAsc();

    /**
     * Finds products sorted by price (descending).
     * 
     * This method supports price-based sorting for product listings.
     * 
     * @return List of products sorted by price (high to low)
     */
    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.price DESC")
    List<Product> findActiveProductsOrderByPriceDesc();

    /**
     * Finds recently added products.
     * 
     * This method is useful for showcasing new arrivals
     * on the homepage or product listing pages.
     * 
     * @param limit the maximum number of products to return
     * @return List of recently added products
     */
    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.createdAt DESC")
    List<Product> findRecentProducts(Pageable pageable);
}
