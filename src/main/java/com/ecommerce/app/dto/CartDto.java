package com.ecommerce.app.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object for Cart operations.
 * 
 * This DTO follows the Single Responsibility Principle by containing
 * only cart-related data for transfer between layers.
 * It prevents entity exposure and provides clean separation
 * between controller and service layers.
 * 
 * The DTO uses validation annotations to ensure data integrity
 * and follows best practices for data transfer objects.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {

    /**
     * Cart ID for update operations.
     * Can be null for create operations.
     */
    private Long id;

    /**
     * User ID associated with the cart.
     * Used for cart ownership verification.
     */
    @NotNull(message = "User ID is required")
    private Long userId;

    /**
     * Flag indicating if the cart is active.
     * Used to determine current shopping cart.
     */
    private Boolean active;

    /**
     * List of cart items in the cart.
     * Used for cart display and calculations.
     */
    private java.util.List<CartItemDto> items;

    /**
     * Total price of all items in the cart.
     * Calculated field for display purposes.
     */
    private BigDecimal totalPrice;

    /**
     * Total number of items in the cart.
     * Calculated field for display purposes.
     */
    private Integer totalItems;

    /**
     * Static factory method for creating CartDto from Cart entity.
     * 
     * This method provides clean conversion between entity and DTO,
     * following the Single Responsibility Principle by centralizing
     * conversion logic.
     * 
     * @param cart Cart entity to convert
     * @return CartDto with cart data
     */
    public static CartDto fromEntity(com.ecommerce.app.entity.Cart cart) {
        if (cart == null) {
            return null;
        }
        
        CartDto dto = new CartDto();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUser() != null ? cart.getUser().getId() : null);
        dto.setActive(cart.getActive());
        
        // Convert cart items
        if (cart.getCartItems() != null) {
            List<CartItemDto> itemDtos = cart.getCartItems().stream()
                    .map(CartItemDto::fromEntity)
                    .collect(java.util.stream.Collectors.toList());
            dto.setItems(itemDtos);
        }
        
        // Calculate totals
        dto.setTotalPrice(cart.getTotalPrice());
        dto.setTotalItems(cart.getTotalItems());
        
        return dto;
    }

    /**
     * Static factory method for creating empty CartDto.
     * 
     * This method provides a clean way to create DTOs
     * for new cart operations.
     * 
     * @param userId user ID for the cart
     * @return empty CartDto with user ID
     */
    public static CartDto createEmpty(Long userId) {
        CartDto dto = new CartDto();
        dto.setId(null);
        dto.setUserId(userId);
        dto.setActive(true);
        dto.setItems(new java.util.ArrayList<>());
        dto.setTotalPrice(BigDecimal.ZERO);
        dto.setTotalItems(0);
        
        return dto;
    }

    /**
     * Static factory method for creating CartDto for checkout.
     * 
     * This method provides a clean way to create DTOs
     * for checkout confirmation pages.
     * 
     * @param cart Cart entity to convert
     * @return CartDto with checkout data
     */
    public static CartDto forCheckout(com.ecommerce.app.entity.Cart cart) {
        CartDto dto = fromEntity(cart);
        
        // Add shipping and tax calculations for checkout display
        if (dto != null) {
            BigDecimal shipping = dto.getTotalPrice().multiply(BigDecimal.valueOf(0.1)); // 10% shipping
            BigDecimal tax = dto.getTotalPrice().multiply(BigDecimal.valueOf(0.08)); // 8% tax
            BigDecimal finalTotal = dto.getTotalPrice().add(shipping).add(tax);
            
            // You could add these as separate fields if needed
            // dto.setShippingCost(shipping);
            // dto.setTaxAmount(tax);
            // dto.setFinalTotal(finalTotal);
        }
        
        return dto;
    }
}
