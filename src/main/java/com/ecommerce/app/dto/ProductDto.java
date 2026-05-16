package com.ecommerce.app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for Product operations.
 * 
 * This DTO follows the Single Responsibility Principle by containing
 * only product-related data for transfer between layers.
 * It prevents entity exposure and provides clean separation
 * between controller and service layers.
 * 
 * The DTO uses validation annotations to ensure data integrity
 * and follows best practices for data transfer objects.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    /**
     * Product ID for update operations.
     * Can be null for create operations.
     */
    private Long id;

    /**
     * Product name for display and search.
     * Must be between 2-100 characters.
     */
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    /**
     * Product description for details and search.
     * Can be null for simple products.
     */
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    /**
     * Product price for display and calculations.
     * Must be positive with 2 decimal places.
     */
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    /**
     * Stock quantity for inventory management.
     * Must be positive integer.
     */
    @NotNull(message = "Stock quantity is required")
    private Integer stockQuantity;

    /**
     * Image URL for product display.
     * Can be null if no image is available.
     */
    @Size(max = 255, message = "Image URL must not exceed 255 characters")
    private String imageUrl;

    /**
     * Flag indicating if product is active.
     * Used for product management and visibility.
     */
    private Boolean active;

    /**
     * Static factory method for creating ProductDto from Product entity.
     * 
     * This method provides clean conversion between entity and DTO,
     * following the Single Responsibility Principle by centralizing
     * conversion logic.
     * 
     * @param product Product entity to convert
     * @return ProductDto with product data
     */
    public static ProductDto fromEntity(com.ecommerce.app.entity.Product product) {
        if (product == null) {
            return null;
        }
        
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setImageUrl(product.getImageUrl());
        dto.setActive(product.isActive());
        
        return dto;
    }

    /**
     * Static factory method for creating ProductDto for creation.
     * 
     * This method provides a clean way to create DTOs
     * for product creation operations.
     * 
     * @param name        product name
     * @param description product description
     * @param price       product price
     * @param stockQuantity product stock quantity
     * @param imageUrl    product image URL
     * @return ProductDto with creation data
     */
    public static ProductDto forCreation(String name, String description, 
                                           BigDecimal price, Integer stockQuantity, 
                                           String imageUrl) {
        ProductDto dto = new ProductDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setPrice(price);
        dto.setStockQuantity(stockQuantity);
        dto.setImageUrl(imageUrl);
        dto.setActive(true); // New products are active by default
        
        return dto;
    }

    /**
     * Static factory method for creating ProductDto for updates.
     * 
     * This method provides a clean way to create DTOs
     * for product update operations.
     * 
     * @param id          product ID for update
     * @param name         product name
     * @param description  product description
     * @param price        product price
     * @param stockQuantity product stock quantity
     * @param imageUrl    product image URL
     * @param active       product active status
     * @return ProductDto with update data
     */
    public static ProductDto forUpdate(Long id, String name, String description, 
                                          BigDecimal price, Integer stockQuantity, 
                                          String imageUrl, Boolean active) {
        ProductDto dto = new ProductDto();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setPrice(price);
        dto.setStockQuantity(stockQuantity);
        dto.setImageUrl(imageUrl);
        dto.setActive(active);
        
        return dto;
    }

    /**
     * Static factory method for creating ProductDto for search results.
     * 
     * This method provides a clean way to create DTOs
     * for product search operations.
     * 
     * @param id          product ID
     * @param name         product name
     * @param description  product description
     * @param price        product price
     * @param imageUrl    product image URL
     * @param active       product active status
     * @return ProductDto with search result data
     */
    public static ProductDto forSearchResult(Long id, String name, String description, 
                                                BigDecimal price, String imageUrl, 
                                                Boolean active) {
        ProductDto dto = new ProductDto();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setPrice(price);
        dto.setStockQuantity(null); // Stock not shown in search results
        dto.setImageUrl(imageUrl);
        dto.setActive(active);
        
        return dto;
    }
}
