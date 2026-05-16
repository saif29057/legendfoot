package com.ecommerce.app.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for CartItem operations.
 * 
 * This DTO follows the Single Responsibility Principle by containing
 * only cart item-related data for transfer between layers.
 * It prevents entity exposure and provides clean separation
 * between controller and service layers.
 * 
 * The DTO uses validation annotations to ensure data integrity
 * and follows best practices for data transfer objects.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {

    /**
     * Cart item ID for update operations.
     * Can be null for create operations.
     */
    private Long id;

    /**
     * Product ID for reference and display.
     * Used to identify the product in the cart item.
     */
    @NotNull(message = "Product ID is required")
    private Long productId;

    /**
     * Product name for display purposes.
     * Used for cart display and search functionality.
     */
    private String productName;

    /**
     * Product image URL for display.
     * Used for cart item visualization.
     */
    private String productImageUrl;

    /**
     * Quantity of the product in the cart.
     * Must be positive integer.
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    /**
     * Unit price of the product at the time of cart addition.
     * Used for price calculations and display.
     */
    private BigDecimal unitPrice;

    /**
     * Subtotal price for this cart item (quantity × unit price).
     * Calculated field for display and checkout.
     */
    private BigDecimal subtotal;

    /**
     * Flag indicating if the item is available for checkout.
     * Based on stock availability and product status.
     */
    private Boolean availableForCheckout;

    /**
     * Static factory method for creating CartItemDto from CartItem entity.
     * 
     * This method provides clean conversion between entity and DTO,
     * following the Single Responsibility Principle by centralizing
     * conversion logic.
     * 
     * @param cartItem CartItem entity to convert
     * @return CartItemDto with cart item data
     */
    public static CartItemDto fromEntity(com.ecommerce.app.entity.CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }
        
        CartItemDto dto = new CartItemDto();
        dto.setId(cartItem.getId());
        dto.setProductId(cartItem.getProduct() != null ? cartItem.getProduct().getId() : null);
        dto.setProductName(cartItem.getDisplayName());
        dto.setProductImageUrl(cartItem.getImageUrl());
        dto.setQuantity(cartItem.getQuantity());
        dto.setUnitPrice(cartItem.getDisplayPrice());
        dto.setSubtotal(cartItem.getSubtotal());
        dto.setAvailableForCheckout(cartItem.isValidForCheckout());
        
        return dto;
    }

    /**
     * Static factory method for creating CartItemDto for cart operations.
     * 
     * This method provides a clean way to create DTOs
     * for adding products to cart.
     * 
     * @param productId product ID to add
     * @param productName  product name for display
     * @param productImageUrl product image URL
     * @param unitPrice     product unit price
     * @param quantity     quantity to add
     * @return CartItemDto with cart addition data
     */
    public static CartItemDto forCartAddition(Long productId, String productName, 
                                                   String productImageUrl, BigDecimal unitPrice, 
                                                   Integer quantity) {
        CartItemDto dto = new CartItemDto();
        dto.setId(null); // New cart item
        dto.setProductId(productId);
        dto.setProductName(productName);
        dto.setProductImageUrl(productImageUrl);
        dto.setUnitPrice(unitPrice);
        dto.setQuantity(quantity);
        dto.setSubtotal(unitPrice.multiply(BigDecimal.valueOf(quantity)));
        dto.setAvailableForCheckout(true); // Assuming product is available
        
        return dto;
    }

    /**
     * Static factory method for creating CartItemDto for updates.
     * 
     * This method provides a clean way to create DTOs
     * for updating cart item quantities.
     * 
     * @param id          existing cart item ID
     * @param productId    product ID
     * @param productName  product name
     * @param productImageUrl product image URL
     * @param unitPrice     product unit price
     * @param quantity     new quantity
     * @return CartItemDto with update data
     */
    public static CartItemDto forUpdate(Long id, Long productId, String productName, 
                                               String productImageUrl, BigDecimal unitPrice, 
                                               Integer quantity) {
        CartItemDto dto = new CartItemDto();
        dto.setId(id);
        dto.setProductId(productId);
        dto.setProductName(productName);
        dto.setProductImageUrl(productImageUrl);
        dto.setUnitPrice(unitPrice);
        dto.setQuantity(quantity);
        dto.setSubtotal(unitPrice.multiply(BigDecimal.valueOf(quantity)));
        dto.setAvailableForCheckout(true); // Assuming product is available
        
        return dto;
    }
}
