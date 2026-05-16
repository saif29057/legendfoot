package com.ecommerce.app.repository;

import com.ecommerce.app.entity.CartItem;
import com.ecommerce.app.entity.Cart;
import com.ecommerce.app.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CartItem entity operations.
 * 
 * This interface follows the Repository pattern and Single Responsibility Principle
 * by being responsible only for data access operations related to CartItem entities.
 * It extends JpaRepository to inherit standard CRUD operations and defines
 * custom query methods for specific cart item-related business requirements.
 * 
 * The interface provides methods for cart item management, supporting shopping
 * cart functionality and inventory tracking.
 * 
 * @Repository annotation indicates this is a Spring repository bean.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Finds cart items by their associated cart.
     * 
     * This method provides cart-based item lookup, commonly used to
     * retrieve all items in a user's shopping cart.
     * 
     * @param cart    cart whose items to find
     * @return List of cart items belonging to the specified cart
     */
    List<CartItem> findByCart(Cart cart);

    /**
     * Finds cart items by their associated product.
     * 
     * This method provides product-based item lookup, useful for
     * tracking which carts contain a specific product.
     * 
     * @param product product whose cart items to find
     * @return List of cart items containing the specified product
     */
    List<CartItem> findByProduct(Product product);

    /**
     * Finds a specific cart item by cart and product.
     * 
     * This method is useful for checking if a product already exists
     * in a cart, and for updating quantities.
     * 
     * @param cart    cart to search in
     * @param product product to search for
     * @return Optional containing the cart item if found, empty otherwise
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart = :cart AND ci.product = :product")
    Optional<CartItem> findByCartAndProduct(@Param("cart") Cart cart, @Param("product") Product product);

    /**
     * Counts the number of items in a specific cart.
     * 
     * This method provides statistical information about cart size,
     * useful for cart management and user interface updates.
     * 
     * @param cart    cart whose items to count
     * @return number of items in the cart
     */
    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.cart = :cart")
    long countByCart(@Param("cart") Cart cart);

    /**
     * Counts the total quantity of all items in a specific cart.
     * 
     * This method calculates the sum of quantities of all items in a cart,
     * providing the total number of products (not just distinct items).
     * 
     * @param cart    cart whose total quantity to calculate
     * @return total quantity of all items in the cart
     */
    @Query("SELECT SUM(ci.quantity) FROM CartItem ci WHERE ci.cart = :cart")
    Long getTotalQuantityByCart(@Param("cart") Cart cart);

    /**
     * Calculates the total price of all items in a specific cart.
     * 
     * This method computes the subtotal for a cart by summing
     * (product price × quantity) for all items.
     * 
     * @param cart    cart whose total price to calculate
     * @return total price of all items in the cart
     */
    @Query("SELECT SUM(ci.product.price * ci.quantity) FROM CartItem ci WHERE ci.cart = :cart")
    java.math.BigDecimal getTotalPriceByCart(@Param("cart") Cart cart);

    /**
     * Finds cart items with quantity greater than available stock.
     * 
     * This method is useful for inventory validation to identify
     * cart items that cannot be fulfilled due to insufficient stock.
     * 
     * @return List of cart items with quantity exceeding available stock
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.quantity > ci.product.stockQuantity")
    List<CartItem> findItemsWithInsufficientStock();

    /**
     * Finds cart items for a specific cart with insufficient stock.
     * 
     * This method is useful for checkout validation to identify
     * items in a user's cart that cannot be fulfilled.
     * 
     * @param cart    cart to check
     * @return List of cart items in the cart with insufficient stock
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart = :cart AND ci.quantity > ci.product.stockQuantity")
    List<CartItem> findItemsWithInsufficientStockByCart(@Param("cart") Cart cart);

    /**
     * Finds cart items for inactive products.
     * 
     * This method is useful for identifying cart items that reference
     * products that are no longer available for purchase.
     * 
     * @return List of cart items for inactive products
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.product.active = false")
    List<CartItem> findItemsForInactiveProducts();

    /**
     * Finds cart items for a specific cart with inactive products.
     * 
     * This method is useful for checkout validation to identify
     * items in a user's cart that are no longer available.
     * 
     * @param cart    cart to check
     * @return List of cart items in the cart for inactive products
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart = :cart AND ci.product.active = false")
    List<CartItem> findItemsForInactiveProductsByCart(@Param("cart") Cart cart);

    /**
     * Finds cart items with quantity greater than specified minimum.
     * 
     * This method is useful for analytics and identifying
     * bulk purchases or popular items.
     * 
     * @param minQuantity minimum quantity threshold
     * @return List of cart items with quantity >= minQuantity
     */
    List<CartItem> findByQuantityGreaterThanEqual(Integer minQuantity);

    /**
     * Finds cart items for a specific cart with quantity greater than specified minimum.
     * 
     * This method is useful for identifying bulk purchases
     * within a specific user's cart.
     * 
     * @param cart       cart to search in
     * @param minQuantity minimum quantity threshold
     * @return List of cart items in the cart with quantity >= minQuantity
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart = :cart AND ci.quantity >= :minQuantity")
    List<CartItem> findByCartAndQuantityGreaterThanEqual(@Param("cart") Cart cart, @Param("minQuantity") Integer minQuantity);

    /**
     * Deletes all cart items for a specific cart.
     * 
     * This method is useful for clearing a cart after checkout
     * or when user wants to start over.
     * 
     * @param cart    cart whose items to delete
     * @return number of deleted items
     */
    long deleteByCart(Cart cart);

    /**
     * Deletes cart items for a specific product.
     * 
     * This method is useful when a product is deleted or deactivated,
     * removing it from all user carts.
     * 
     * @param product product whose cart items to delete
     * @return number of deleted items
     */
    long deleteByProduct(Product product);

    /**
     * Checks if a cart contains a specific product.
     * 
     * This method is useful for quick validation to determine
     * if a product is already in a user's cart.
     * 
     * @param cart    cart to check
     * @param product product to check for
     * @return true if cart contains the product, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(ci) > 0 THEN true ELSE false END FROM CartItem ci WHERE ci.cart = :cart AND ci.product = :product")
    boolean existsByCartAndProduct(@Param("cart") Cart cart, @Param("product") Product product);

    /**
     * Finds cart items sorted by creation date (newest first).
     * 
     * This method is useful for displaying cart items in the order
     * they were added by the user.
     * 
     * @param cart    cart whose items to find
     * @return List of cart items sorted by creation date (newest first)
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart = :cart ORDER BY ci.createdAt DESC")
    List<CartItem> findByCartOrderByCreatedAtDesc(@Param("cart") Cart cart);

    /**
     * Finds all cart items across all carts for analytics.
     * 
     * This method provides a complete list of all cart items,
     * useful for administrative and analytical purposes.
     * 
     * @return List of all cart items
     */
    @Query("SELECT ci FROM CartItem ci ORDER BY ci.updatedAt DESC")
    List<CartItem> findAllCartItems();
}
