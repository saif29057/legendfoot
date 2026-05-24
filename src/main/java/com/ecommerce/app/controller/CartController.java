package com.ecommerce.app.controller;

import com.ecommerce.app.dto.CartDto;
import com.ecommerce.app.dto.CartItemDto;
import com.ecommerce.app.service.CartService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for handling cart-related operations.
 *
 * This controller follows SOLID principles:
 * - Single Responsibility: Handles only cart-related web requests
 * - Open/Closed: Open for extension through additional endpoints
 * - Liskov Substitution: Can be substituted with any CartController implementation
 * - Interface Segregation: Provides only cart-related methods
 * - Dependency Inversion: Depends on CartService interface, not concrete classes
 *
 * The class provides a clean separation between web layer
 * and business logic, following best practices for controller design.
 */
@Controller
@RequestMapping("/cart")
@Slf4j
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Displays user's shopping cart.
     *
     * This method shows the current user's cart with
     * all items and calculated totals.
     *
     * @param model for view attributes
     * @return name of cart view template
     */
    @GetMapping
    public String showCart(Model model) {
        log.debug("Displaying user cart");

        try {
            CartDto cart = cartService.getUserCart();
            model.addAttribute("cart", cart);
            model.addAttribute("pageTitle", "Shopping Cart");
            model.addAttribute("currentPage", "cart");

            return "cart/view";

        } catch (Exception e) {
            log.error("Error loading cart: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Unable to load cart: " + e.getMessage());
            model.addAttribute("pageTitle", "Shopping Cart");
            model.addAttribute("currentPage", "cart");

            return "cart/view";
        }
    }

    /**
     * Adds product to cart.
     *
     * This method handles adding products to cart with
     * validation and provides appropriate feedback to the user.
     * Accepts both GET and POST requests for flexibility.
     *
     * @param productId product ID to add
     * @param quantity quantity to add
     * @param redirectAttributes for flash messages
     * @return redirect to cart view on success
     */
    @RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
    public String addToCart(
            @RequestParam(required = true) Long productId,
            @RequestParam(required = true, defaultValue = "1") Integer quantity,
            RedirectAttributes redirectAttributes) {

        log.debug("Adding product to cart: {}", productId);

        // Validate quantity
        if (quantity == null || quantity <= 0) {
            log.warn("Cart addition validation failed: invalid quantity {}", quantity);
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid quantity.");
            return "redirect:/products/" + productId;
        }

        try {
            cartService.addProductToCart(productId, quantity);
            log.info("Product added to cart: {}", productId);
            redirectAttributes.addFlashAttribute("successMessage", "Product added to cart!");
            return "redirect:/cart";

        } catch (Exception e) {
            log.error("Failed to add product to cart: {}", productId, e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Could not add to cart: " + e.getMessage());
            return "redirect:/products/" + productId;
        }
    }

    /**
     * Updates cart item quantity.
     *
     * This method handles updating quantity of items in cart
     * with validation and provides appropriate feedback.
     *
     * @param cartItemDto cart item data
     * @param bindingResult validation results
     * @param redirectAttributes for flash messages
     * @param model for view attributes
     * @return redirect to cart view on success
     */
    @PostMapping("/update")
    public String updateCartItem(
            @Valid @ModelAttribute("cartItemDto") CartItemDto cartItemDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        log.debug("Updating cart item: {}", cartItemDto.getProductId());

        if (bindingResult.hasErrors()) {
            log.warn("Cart update validation failed for product: {}", cartItemDto.getProductId());
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid quantity.");
            return "redirect:/cart";
        }

        try {
            cartService.updateCartItemQuantity(cartItemDto.getProductId(), cartItemDto.getQuantity());
            log.info("Cart item updated: {}", cartItemDto.getProductId());
            redirectAttributes.addFlashAttribute("successMessage", "Cart updated!");
            return "redirect:/cart";

        } catch (Exception e) {
            log.error("Failed to update cart item: {}", cartItemDto.getProductId(), e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Could not update cart: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    /**
     * Removes item from cart.
     *
     * This method handles removing items from cart
     * and provides appropriate feedback to the user.
     *
     * @param productId product ID to remove
     * @param redirectAttributes for flash messages
     * @return redirect to cart view on success
     */
    @PostMapping("/remove/{productId}")
    public String removeFromCart(
            @PathVariable Long productId,
            RedirectAttributes redirectAttributes) {

        log.debug("Removing product from cart: {}", productId);

        try {
            cartService.removeProductFromCart(productId);
            log.info("Product removed from cart: {}", productId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Product removed from cart successfully!");
            return "redirect:/cart";

        } catch (Exception e) {
            log.error("Failed to remove product from cart: {}", productId, e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to remove product from cart: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    /**
     * Clears entire cart.
     *
     * This method handles clearing all items from cart
     * and provides appropriate feedback to the user.
     *
     * @param redirectAttributes for flash messages
     * @return redirect to cart view on success
     */
    @PostMapping("/clear")
    public String clearCart(RedirectAttributes redirectAttributes) {
        log.debug("Clearing user cart");

        try {
            cartService.clearCart();
            log.info("Cart cleared successfully");
            redirectAttributes.addFlashAttribute("successMessage",
                    "Cart cleared successfully!");
            return "redirect:/cart";

        } catch (Exception e) {
            log.error("Failed to clear cart: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to clear cart: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    /**
     * Displays checkout page.
     *
     * This method shows checkout page with cart summary
     * and order form for finalizing purchase.
     *
     * @param model for view attributes
     * @param redirectAttributes for flash attributes on redirect
     * @return name of checkout view template
     */
    @GetMapping("/checkout")
    public String showCheckout(Model model, RedirectAttributes redirectAttributes) {
        log.debug("Displaying checkout page");

        try {
            CartDto cart = cartService.getUserCart();
            if (cart.getItems().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Your cart is empty. Please add items before checkout.");
                return "redirect:/cart";
            }

            model.addAttribute("cart", cart);
            model.addAttribute("pageTitle", "Checkout");
            model.addAttribute("currentPage", "checkout");

            return "cart/checkout";

        } catch (Exception e) {
            log.error("Error loading checkout page: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Unable to load checkout: " + e.getMessage());
            model.addAttribute("pageTitle", "Checkout");
            model.addAttribute("currentPage", "checkout");

            return "cart/checkout";
        }
    }

    /**
     * Processes checkout.
     *
     * This method handles the checkout process, creating an order
     * from cart items and providing appropriate feedback.
     *
     * @param redirectAttributes for flash messages
     * @return redirect to order confirmation on success
     */
    @PostMapping("/checkout")
    public String processCheckout(RedirectAttributes redirectAttributes) {
        log.debug("Processing checkout");

        try {
            cartService.checkout();
            log.info("Checkout processed successfully");
            redirectAttributes.addFlashAttribute("successMessage",
                    "Order placed successfully! Thank you for your purchase.");
            return "redirect:/orders";

        } catch (Exception e) {
            log.error("Checkout failed: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Checkout failed: " + e.getMessage());
            return "redirect:/cart/checkout";
        }
    }
}
