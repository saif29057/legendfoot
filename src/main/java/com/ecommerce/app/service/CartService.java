package com.ecommerce.app.service;

import com.ecommerce.app.dto.CartDto;
import com.ecommerce.app.entity.Cart;
import com.ecommerce.app.entity.CartItem;
import com.ecommerce.app.entity.Product;
import com.ecommerce.app.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Cart business operations.
 * 
 * This interface follows Single Responsibility Principle by defining only
 * cart-related business operations. It also follows the Interface
 * Segregation Principle by providing only methods relevant to cart management.
 * 
 * The interface uses dependency inversion by allowing implementations to be
 * injected without depending on concrete classes.
 */
public interface CartService {

    /**
     * Creates a new cart for a user.
     * 
     * This method handles cart creation with proper initialization
     * and validation of user data.
     * 
     * @param user user to create cart for
     * @return created cart
     * @throws IllegalArgumentException if user is null
     * @throws RuntimeException if user already has active cart
     */
    Cart createCart(User user);

    /**
     * Retrieves or creates an active cart for a user.
     * 
     * This method ensures each user has exactly one active cart,
     * creating one if it doesn't exist.
     * 
     * @param user user to get cart for
     * @return active cart for the user
     * @throws IllegalArgumentException if user is null
     */
    Cart getOrCreateActiveCart(User user);

    /**
     * Retrieves a cart by its ID.
     * 
     * @param id ID of cart to retrieve
     * @return Optional containing cart if found, empty otherwise
     */
    Optional<Cart> getCartById(Long id);

    /**
     * Retrieves the active cart for a specific user.
     * 
     * @param user user whose active cart to retrieve
     * @return Optional containing active cart if found, empty otherwise
     */
    Optional<Cart> getActiveCartByUser(User user);

    /**
     * Retrieves the current user's cart as a DTO.
     * 
     * This method gets the currently authenticated user's active cart
     * and converts it to a CartDto for controller use.
     * 
     * @return CartDto containing user's cart data
     * @throws RuntimeException if user is not authenticated or cart retrieval fails
     */
    CartDto getUserCart();

    /**
     * Adds a product to the current user's cart by product ID.
     * 
     * This method handles adding products to cart with proper
     * stock validation and quantity management.
     * 
     * @param productId product ID to add
     * @param quantity quantity to add
     * @throws IllegalArgumentException if parameters are invalid
     * @throws RuntimeException if insufficient stock or product not found
     */
    void addProductToCart(Long productId, Integer quantity);

    /**
     * Updates cart item quantity for the current user by product ID.
     * 
     * This method handles updating quantity of items in cart
     * with validation and provides appropriate feedback.
     * 
     * @param productId product ID to update
     * @param quantity new quantity
     * @throws IllegalArgumentException if parameters are invalid
     * @throws RuntimeException if insufficient stock or product not found
     */
    void updateCartItemQuantity(Long productId, Integer quantity);

    /**
     * Removes a product from the current user's cart by product ID.
     * 
     * @param productId product ID to remove
     * @throws RuntimeException if product not found in cart
     */
    void removeProductFromCart(Long productId);

    /**
     * Clears all items from the current user's active cart.
     * 
     * @throws RuntimeException if no active cart found
     */
    void clearCart();

    /**
     * Processes checkout for the current user's cart.
     * 
     * This method handles the checkout process, creating an order
     * from cart items and clearing the cart.
     * 
     * @throws RuntimeException if checkout fails
     */
    void checkout();

    /**
     * Adds a product to the user's cart.
     * 
     * This method handles adding products to cart with proper
     * stock validation and quantity management.
     * 
     * @param user    user whose cart to add to
     * @param product product to add
     * @param quantity quantity to add
     * @return updated cart
     * @throws IllegalArgumentException if parameters are invalid
     * @throws RuntimeException if insufficient stock
     */
    Cart addProductToCart(User user, Product product, Integer quantity);

    /**
     * Updates the quantity of a product in the user's cart.
     * 
     * @param user    user whose cart to update
     * @param product product to update
     * @param quantity new quantity
     * @return updated cart
     * @throws IllegalArgumentException if parameters are invalid
     * @throws RuntimeException if insufficient stock or product not in cart
     */
    Cart updateProductQuantity(User user, Product product, Integer quantity);

    /**
     * Removes a product from the user's cart.
     * 
     * @param user    user whose cart to remove from
     * @param product product to remove
     * @return updated cart
     * @throws RuntimeException if product not found in cart
     */
    Cart removeProductFromCart(User user, Product product);

    /**
     * Removes a specific cart item from the user's cart.
     * 
     * @param user     user whose cart to remove from
     * @param cartItem cart item to remove
     * @return updated cart
     * @throws RuntimeException if cart item not found
     */
    Cart removeCartItem(User user, CartItem cartItem);

    /**
     * Clears all items from the user's active cart.
     * 
     * This method is typically used after successful checkout
     * or when user wants to start over.
     * 
     * @param user user whose cart to clear
     * @return cleared cart
     */
    Cart clearCart(User user);

    /**
     * Retrieves all cart items for a user.
     * 
     * @param user user whose cart items to retrieve
     * @return List of cart items in user's active cart
     */
    List<CartItem> getCartItems(User user);

    /**
     * Retrieves a specific cart item.
     * 
     * @param id ID of cart item to retrieve
     * @return Optional containing cart item if found, empty otherwise
     */
    Optional<CartItem> getCartItemById(Long id);

    /**
     * Calculates the total price of items in the user's cart.
     * 
     * @param user user whose cart total to calculate
     * @return total price as BigDecimal
     */
    java.math.BigDecimal getCartTotal(User user);

    /**
     * Calculates the total number of items in the user's cart.
     * 
     * @param user user whose cart item count to calculate
     * @return total number of items
     */
    int getCartItemCount(User user);

    /**
     * Checks if the user's cart contains a specific product.
     * 
     * @param user    user whose cart to check
     * @param product product to check for
     * @return true if product is in cart, false otherwise
     */
    boolean isProductInCart(User user, Product product);

    /**
     * Gets the quantity of a specific product in the user's cart.
     * 
     * @param user    user whose cart to check
     * @param product product to get quantity for
     * @return quantity of product in cart, or 0 if not found
     */
    Integer getProductQuantity(User user, Product product);

    /**
     * Validates that all items in the user's cart are available for checkout.
     * 
     * This method checks stock availability and product status
     * to ensure checkout can proceed successfully.
     * 
     * @param user user whose cart to validate
     * @return true if all items are valid for checkout, false otherwise
     */
    boolean isCartValidForCheckout(User user);

    /**
     * Retrieves validation errors for the user's cart.
     * 
     * This method provides detailed information about why
     * a cart might not be valid for checkout.
     * 
     * @param user user whose cart to validate
     * @return List of validation error messages
     */
    List<String> getCartValidationErrors(User user);

    /**
     * Merges a guest cart with a user's cart after login.
     * 
     * This method handles the transition from guest shopping
     * to authenticated user shopping.
     * 
     * @param user       authenticated user
     * @param guestCart  cart from guest session
     * @return merged cart
     */
    Cart mergeGuestCart(User user, Cart guestCart);

    /**
     * Deactivates the user's active cart.
     * 
     * This method is used after checkout to mark the cart
     * as inactive while preserving it for historical purposes.
     * 
     * @param user user whose cart to deactivate
     * @return deactivated cart
     */
    Cart deactivateCart(User user);

    /**
     * Retrieves all carts for a user (including inactive).
     * 
     * This method is useful for order history and
     * cart analytics.
     * 
     * @param user user whose carts to retrieve
     * @return List of all carts for the user
     */
    List<Cart> getAllCartsForUser(User user);

    /**
     * Deletes old inactive carts to clean up the database.
     * 
     * This method is used for maintenance to remove
     * old cart data and free up storage space.
     * 
     * @param daysOld minimum age in days for carts to be deleted
     * @return number of carts deleted
     */
    int cleanupOldCarts(int daysOld);

    /**
     * Retrieves carts that have been abandoned (inactive for specified time).
     * 
     * This method is useful for analytics and recovery
     * of potentially lost sales.
     * 
     * @param hoursInactive number of hours of inactivity
     * @return List of abandoned carts
     */
    List<Cart> getAbandonedCarts(int hoursInactive);

    /**
     * Applies a discount code to the user's cart.
     * 
     * This method handles promotional discounts and
     * special offers for the cart.
     * 
     * @param user         user whose cart to apply discount to
     * @param discountCode discount code to apply
     * @return updated cart with discount applied
     * @throws RuntimeException if discount code is invalid
     */
    Cart applyDiscountCode(User user, String discountCode);

    /**
     * Removes any applied discount from the user's cart.
     * 
     * @param user user whose cart discount to remove
     * @return cart without discount
     */
    Cart removeDiscount(User user);

    /**
     * Estimates shipping cost for the user's cart.
     * 
     * This method calculates shipping based on cart contents,
     * destination, and shipping method preferences.
     * 
     * @param user user whose cart shipping to estimate
     * @return estimated shipping cost
     */
    java.math.BigDecimal estimateShipping(User user);

    /**
     * Calculates tax for the user's cart.
     * 
     * This method calculates tax based on cart total,
     * user location, and applicable tax rates.
     * 
     * @param user user whose cart tax to calculate
     * @return calculated tax amount
     */
    java.math.BigDecimal calculateTax(User user);

    /**
     * Gets the final total including tax and shipping.
     * 
     * @param user user whose final total to calculate
     * @return final total amount
     */
    java.math.BigDecimal getFinalTotal(User user);
}
