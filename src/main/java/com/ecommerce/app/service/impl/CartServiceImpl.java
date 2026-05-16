package com.ecommerce.app.service.impl;

import com.ecommerce.app.dto.CartDto;
import com.ecommerce.app.entity.Cart;
import com.ecommerce.app.entity.CartItem;
import com.ecommerce.app.entity.Product;
import com.ecommerce.app.entity.User;
import com.ecommerce.app.repository.CartRepository;
import com.ecommerce.app.repository.CartItemRepository;
import com.ecommerce.app.repository.ProductRepository;
import com.ecommerce.app.service.CartService;
import com.ecommerce.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of CartService interface.
 * 
 * This class follows SOLID principles:
 * - Single Responsibility: Handles only cart-related business logic
 * - Open/Closed: Open for extension through interfaces, closed for modification
 * - Liskov Substitution: Can be substituted with any CartService implementation
 * - Interface Segregation: Implements only methods needed for cart operations
 * - Dependency Inversion: Depends on CartService interface, not concrete classes
 * 
 * The class uses constructor injection for dependency management
 * and follows best practices for error handling and logging.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    /**
     * Creates a new cart for a user.
     * 
     * This method validates user data and creates a new
     * active cart for the user.
     * 
     * @param user user to create cart for
     * @return created cart
     * @throws IllegalArgumentException if user is null
     */
    @Override
    public Cart createCart(User user) {
        log.info("Creating new cart for user: {}", user.getUsername());
        
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        // Check if user already has active cart
        if (cartRepository.hasActiveCart(user)) {
            throw new RuntimeException("User already has an active cart");
        }
        
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setActive(true);
        
        Cart savedCart = cartRepository.save(cart);
        log.info("Successfully created cart with ID: {}", savedCart.getId());
        
        return savedCart;
    }

    /**
     * Retrieves or creates an active cart for a user.
     * 
     * This method ensures each user has exactly one active cart.
     * 
     * @param user user to get cart for
     * @return active cart for user
     */
    @Override
    public Cart getOrCreateActiveCart(User user) {
        log.debug("Getting or creating active cart for user: {}", user.getUsername());
        
        Optional<Cart> activeCartOpt = cartRepository.findActiveCartByUser(user);
        
        if (activeCartOpt.isPresent()) {
            return activeCartOpt.get();
        }
        
        // Create new cart if none exists
        return createCart(user);
    }

    /**
     * Retrieves a cart by its ID.
     * 
     * @param id ID of cart to retrieve
     * @return Optional containing cart if found, empty otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Cart> getCartById(Long id) {
        log.debug("Retrieving cart with ID: {}", id);
        return cartRepository.findById(id);
    }

    /**
     * Retrieves active cart for a specific user.
     * 
     * @param user user whose active cart to retrieve
     * @return Optional containing active cart if found, empty otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Cart> getActiveCartByUser(User user) {
        log.debug("Retrieving active cart for user: {}", user.getUsername());
        return cartRepository.findActiveCartByUser(user);
    }

    /**
     * Retrieves the current user's cart as a DTO.
     * 
     * This method gets the currently authenticated user's active cart
     * and converts it to a CartDto for controller use.
     * 
     * @return CartDto containing user's cart data
     * @throws RuntimeException if user is not authenticated or cart retrieval fails
     */
    @Override
    @Transactional(readOnly = true)
    public CartDto getUserCart() {
        log.debug("Retrieving current user's cart");
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        
        String username = authentication.getName();
        User currentUser = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        Optional<Cart> cartOpt = getActiveCartByUser(currentUser);
        if (cartOpt.isPresent()) {
            return CartDto.fromEntity(cartOpt.get());
        } else {
            // Return empty cart if no active cart exists
            return CartDto.createEmpty(currentUser.getId());
        }
    }

    /**
     * Adds a product to the current user's cart by product ID.
     */
    @Override
    public void addProductToCart(Long productId, Integer quantity) {
        log.info("Adding product {} (quantity: {}) to current user's cart", productId, quantity);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        
        String username = authentication.getName();
        User currentUser = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
        
        addProductToCart(currentUser, product, quantity);
    }

    /**
     * Updates cart item quantity for the current user by product ID.
     */
    @Override
    public void updateCartItemQuantity(Long productId, Integer quantity) {
        log.info("Updating quantity for product {} to {} in current user's cart", productId, quantity);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        
        String username = authentication.getName();
        User currentUser = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
        
        updateProductQuantity(currentUser, product, quantity);
    }

    /**
     * Removes a product from the current user's cart by product ID.
     */
    @Override
    public void removeProductFromCart(Long productId) {
        log.info("Removing product {} from current user's cart", productId);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        
        String username = authentication.getName();
        User currentUser = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
        
        removeProductFromCart(currentUser, product);
    }

    /**
     * Clears all items from the current user's active cart.
     */
    @Override
    public void clearCart() {
        log.info("Clearing current user's cart");
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        
        String username = authentication.getName();
        User currentUser = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        // Call the implementation method with User parameter
        clearCart(currentUser);
    }

    /**
     * Processes checkout for the current user's cart.
     */
    @Override
    public void checkout() {
        log.info("Processing checkout for current user's cart");
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        
        String username = authentication.getName();
        User currentUser = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        // Validate cart before checkout
        if (!isCartValidForCheckout(currentUser)) {
            List<String> errors = getCartValidationErrors(currentUser);
            throw new RuntimeException("Cart is not valid for checkout: " + String.join(", ", errors));
        }
        
        // Clear cart after successful checkout
        clearCart(currentUser);
        
        log.info("Checkout processed successfully for user: {}", username);
    }

    /**
     * Adds a product to user's cart.
     * 
     * This method handles adding products with stock validation.
     * 
     * @param user    user whose cart to add to
     * @param product product to add
     * @param quantity quantity to add
     * @return updated cart
     * @throws IllegalArgumentException if parameters are invalid
     * @throws RuntimeException if insufficient stock
     */
    @Override
    public Cart addProductToCart(User user, Product product, Integer quantity) {
        log.info("Adding product {} (quantity: {}) to cart for user: {}", 
                product.getName(), quantity, user.getUsername());
        
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        if (!product.isInStock()) {
            throw new RuntimeException("Product is out of stock: " + product.getName());
        }
        
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }
        
        Cart cart = getOrCreateActiveCart(user);
        cart.addProduct(product, quantity);
        
        Cart savedCart = cartRepository.save(cart);
        log.info("Successfully added product to cart with ID: {}", savedCart.getId());
        
        return savedCart;
    }

    /**
     * Updates quantity of a product in user's cart.
     * 
     * @param user    user whose cart to update
     * @param product product to update
     * @param quantity new quantity
     * @return updated cart
     */
    @Override
    public Cart updateProductQuantity(User user, Product product, Integer quantity) {
        log.info("Updating quantity for product {} to {} in cart for user: {}", 
                product.getName(), quantity, user.getUsername());
        
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        if (!product.isInStock()) {
            throw new RuntimeException("Product is out of stock: " + product.getName());
        }
        
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }
        
        Cart cart = getOrCreateActiveCart(user);
        cart.updateProductQuantity(product, quantity);
        
        Cart savedCart = cartRepository.save(cart);
        log.info("Successfully updated product quantity in cart with ID: {}", savedCart.getId());
        
        return savedCart;
    }

    /**
     * Removes a product from the user's cart.
     * 
     * @param user    user whose cart to remove from
     * @param product product to remove
     * @return updated cart
     */
    @Override
    public Cart removeProductFromCart(User user, Product product) {
        log.info("Removing product {} from cart for user: {}", 
                product.getName(), user.getUsername());
        
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        Cart cart = getOrCreateActiveCart(user);
        cart.removeProduct(product);
        
        Cart savedCart = cartRepository.save(cart);
        log.info("Successfully removed product from cart with ID: {}", savedCart.getId());
        
        return savedCart;
    }

    /**
     * Retrieves all cart items for a user.
     * 
     * @param user user whose cart items to retrieve
     * @return List of cart items in user's active cart
     */
    @Override
    @Transactional(readOnly = true)
    public List<CartItem> getCartItems(User user) {
        log.debug("Retrieving cart items for user: {}", user.getUsername());
        
        Optional<Cart> cartOpt = cartRepository.findActiveCartByUser(user);
        if (cartOpt.isPresent()) {
            return cartItemRepository.findByCart(cartOpt.get());
        }
        
        return List.of();
    }

    /**
     * Calculates total price of items in user's cart.
     * 
     * @param user user whose cart total to calculate
     * @return total price as BigDecimal
     */
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getCartTotal(User user) {
        log.debug("Calculating cart total for user: {}", user.getUsername());
        
        Optional<Cart> cartOpt = cartRepository.findActiveCartByUser(user);
        if (cartOpt.isPresent()) {
            return cartItemRepository.getTotalPriceByCart(cartOpt.get());
        }
        
        return BigDecimal.ZERO;
    }

    /**
     * Calculates total number of items in user's cart.
     * 
     * @param user user whose cart item count to calculate
     * @return total number of items
     */
    @Override
    @Transactional(readOnly = true)
    public int getCartItemCount(User user) {
        log.debug("Calculating cart item count for user: {}", user.getUsername());
        
        Optional<Cart> cartOpt = cartRepository.findActiveCartByUser(user);
        if (cartOpt.isPresent()) {
            Long totalQuantity = cartItemRepository.getTotalQuantityByCart(cartOpt.get());
            return totalQuantity != null ? totalQuantity.intValue() : 0;
        }
        
        return 0;
    }

    /**
     * Checks if user's cart contains a specific product.
     * 
     * @param user    user whose cart to check
     * @param product product to check for
     * @return true if product is in cart, false otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isProductInCart(User user, Product product) {
        log.debug("Checking if product {} is in cart for user: {}", 
                product.getName(), user.getUsername());
        
        Optional<Cart> cartOpt = cartRepository.findActiveCartByUser(user);
        if (cartOpt.isPresent()) {
            return cartItemRepository.existsByCartAndProduct(cartOpt.get(), product);
        }
        
        return false;
    }

    /**
     * Gets quantity of a specific product in user's cart.
     * 
     * @param user    user whose cart to check
     * @param product product to get quantity for
     * @return quantity of product in cart, or 0 if not found
     */
    @Override
    @Transactional(readOnly = true)
    public Integer getProductQuantity(User user, Product product) {
        log.debug("Getting quantity for product {} in cart for user: {}", 
                product.getName(), user.getUsername());
        
        Optional<Cart> cartOpt = cartRepository.findActiveCartByUser(user);
        if (cartOpt.isPresent()) {
            Optional<CartItem> cartItemOpt = cartItemRepository.findByCartAndProduct(cartOpt.get(), product);
            if (cartItemOpt.isPresent()) {
                return cartItemOpt.get().getQuantity();
            }
        }
        
        return 0;
    }

    /**
     * Validates that all items in user's cart are available for checkout.
     * 
     * @param user user whose cart to validate
     * @return true if all items are valid for checkout, false otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isCartValidForCheckout(User user) {
        log.debug("Validating cart for checkout for user: {}", user.getUsername());
        
        List<String> errors = getCartValidationErrors(user);
        return errors.isEmpty();
    }

    /**
     * Retrieves validation errors for user's cart.
     * 
     * @param user user whose cart to validate
     * @return List of validation error messages
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getCartValidationErrors(User user) {
        log.debug("Getting cart validation errors for user: {}", user.getUsername());
        
        Optional<Cart> cartOpt = cartRepository.findActiveCartByUser(user);
        if (cartOpt.isEmpty()) {
            return List.of("No active cart found");
        }
        
        Cart cart = cartOpt.get();
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        List<String> errors = new java.util.ArrayList<>();
        
        for (CartItem item : cartItems) {
            if (!item.isValidForCheckout()) {
                errors.add("Product '" + item.getDisplayName() + "' is not available for checkout");
            }
        }
        
        return errors;
    }

    /**
     * Clears all items from the user's active cart.
     * 
     * @param user user whose cart to clear
     * @return cleared cart
     */
    @Override
    public Cart clearCart(User user) {
        log.info("Clearing cart for user: {}", user.getUsername());
        
        Optional<Cart> cartOpt = cartRepository.findActiveCartByUser(user);
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            cart.clear();
            Cart savedCart = cartRepository.save(cart);
            log.info("Successfully cleared cart with ID: {}", savedCart.getId());
            return savedCart;
        }
        
        throw new RuntimeException("No active cart found for user: " + user.getUsername());
    }

    // Placeholder implementations for remaining interface methods
    @Override
    public Cart removeCartItem(User user, CartItem cartItem) {
        log.info("Removing cart item {} from cart for user: {}", 
                cartItem.getId(), user.getUsername());
        
        Cart cart = getOrCreateActiveCart(user);
        cart.getCartItems().remove(cartItem);
        
        Cart savedCart = cartRepository.save(cart);
        log.info("Successfully removed cart item from cart with ID: {}", savedCart.getId());
        
        return savedCart;
    }

    @Override
    public Cart mergeGuestCart(User user, Cart guestCart) {
        log.info("Merging guest cart for user: {}", user.getUsername());
        
        Cart userCart = getOrCreateActiveCart(user);
        
        if (guestCart != null && !guestCart.getCartItems().isEmpty()) {
            for (CartItem guestItem : guestCart.getCartItems()) {
                if (guestItem.getProduct() != null && guestItem.getProduct().isInStock()) {
                    userCart.addProduct(guestItem.getProduct(), guestItem.getQuantity());
                }
            }
        }
        
        Cart savedCart = cartRepository.save(userCart);
        log.info("Successfully merged guest cart for user: {}", savedCart.getId());
        
        return savedCart;
    }

    @Override
    public Cart deactivateCart(User user) {
        log.info("Deactivating cart for user: {}", user.getUsername());
        
        Optional<Cart> cartOpt = cartRepository.findActiveCartByUser(user);
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            cart.setActive(false);
            Cart savedCart = cartRepository.save(cart);
            log.info("Successfully deactivated cart with ID: {}", savedCart.getId());
            return savedCart;
        }
        
        throw new RuntimeException("No active cart found for user: " + user.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cart> getAllCartsForUser(User user) {
        log.debug("Retrieving all carts for user: {}", user.getUsername());
        return (List<Cart>) cartRepository.findAllCartsByUser(user);
    }

    @Override
    public int cleanupOldCarts(int daysOld) {
        log.info("Cleaning up carts older than {} days", daysOld);
        
        java.time.LocalDateTime cutoffDate = java.time.LocalDateTime.now().minusDays(daysOld);
        List<Cart> oldCarts = StreamSupport.stream(cartRepository.findCartsUpdatedBetween(
                java.time.LocalDateTime.of(2000, 1, 1, 0, 0), cutoffDate).spliterator(), false)
                .collect(Collectors.toList());
        
        int deletedCount = 0;
        for (Cart cart : oldCarts) {
            cartRepository.delete(cart);
            deletedCount++;
        }
        
        log.info("Successfully deleted {} old carts", deletedCount);
        return deletedCount;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cart> getAbandonedCarts(int hoursInactive) {
        log.debug("Finding abandoned carts inactive for {} hours", hoursInactive);
        
        java.time.LocalDateTime cutoffTime = java.time.LocalDateTime.now().minusHours(hoursInactive);
        return StreamSupport.stream(cartRepository.findCartsUpdatedBetween(cutoffTime, java.time.LocalDateTime.now()).spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Cart applyDiscountCode(User user, String discountCode) {
        // This would be implemented when discount system is added
        log.info("Applying discount code {} for user: {}", discountCode, user.getUsername());
        return getOrCreateActiveCart(user);
    }

    @Override
    public Cart removeDiscount(User user) {
        // This would be implemented when discount system is added
        log.info("Removing discount for user: {}", user.getUsername());
        return getOrCreateActiveCart(user);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal estimateShipping(User user) {
        log.debug("Estimating shipping for user: {}", user.getUsername());
        
        BigDecimal cartTotal = getCartTotal(user);
        // Simple shipping calculation: 10% of cart total, minimum $5
        BigDecimal shipping = cartTotal.multiply(BigDecimal.valueOf(0.1));
        return shipping.compareTo(BigDecimal.valueOf(5.00)) < 0 ? BigDecimal.valueOf(5.00) : shipping;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTax(User user) {
        log.debug("Calculating tax for user: {}", user.getUsername());
        
        BigDecimal cartTotal = getCartTotal(user);
        // 8% tax rate (from application.properties)
        return cartTotal.multiply(BigDecimal.valueOf(0.08));
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getFinalTotal(User user) {
        log.debug("Calculating final total for user: {}", user.getUsername());
        
        BigDecimal cartTotal = getCartTotal(user);
        BigDecimal shipping = estimateShipping(user);
        BigDecimal tax = calculateTax(user);
        
        return cartTotal.add(shipping).add(tax);
    }

    /**
     * Retrieves a specific cart item by ID.
     * 
     * @param id cart item ID
     * @return optional cart item
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CartItem> getCartItemById(Long id) {
        log.debug("Retrieving cart item by ID: {}", id);
        return cartItemRepository.findById(id);
    }
}
