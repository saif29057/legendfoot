package com.ecommerce.app.service;

import com.ecommerce.app.dto.ProductDto;
import com.ecommerce.app.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Product business operations.
 * 
 * This interface follows Single Responsibility Principle by defining only
 * product-related business operations. It also follows the Interface
 * Segregation Principle by providing only methods relevant to product management.
 * 
 * The interface uses dependency inversion by allowing implementations to be
 * injected without depending on concrete classes.
 */
public interface ProductService {

    /**
     * Creates a new product in the system.
     * 
     * This method handles product creation with validation of
     * product data and business rules enforcement.
     * 
     * @param product product to create
     * @return created product
     * @throws IllegalArgumentException if product data is invalid
     * @throws RuntimeException if product name already exists
     */
    Product createProduct(Product product);

    /**
     * Creates a new product from DTO.
     * 
     * This method handles product creation from DTO with validation of
     * product data and business rules enforcement.
     * 
     * @param productDto product DTO to create
     * @return created product
     * @throws IllegalArgumentException if product data is invalid
     * @throws RuntimeException if product name already exists
     */
    Product createProduct(ProductDto productDto);

    /**
     * Updates an existing product.
     * 
     * This method handles product updates while maintaining
     * data integrity and business constraints.
     * 
     * @param id      ID of product to update
     * @param product updated product data
     * @return updated product
     * @throws IllegalArgumentException if product data is invalid
     * @throws RuntimeException if product not found
     */
    Product updateProduct(Long id, Product product);

    /**
     * Updates an existing product from DTO.
     * 
     * This method handles product updates from DTO while maintaining
     * data integrity and business constraints.
     * 
     * @param id         ID of product to update
     * @param productDto updated product DTO data
     * @return updated product
     * @throws IllegalArgumentException if product data is invalid
     * @throws RuntimeException if product not found
     */
    Product updateProduct(Long id, ProductDto productDto);

    /**
     * Deletes a product by their ID.
     * 
     * This method handles product deletion with proper cleanup
     * of related data and maintaining referential integrity.
     * 
     * @param id ID of product to delete
     * @throws RuntimeException if product not found
     */
    void deleteProduct(Long id);

    /**
     * Retrieves a product by their ID.
     * 
     * @param id ID of product to retrieve
     * @return Optional containing product if found, empty otherwise
     */
    Optional<Product> getProductById(Long id);

    /**
     * Retrieves a product by their name.
     * 
     * @param name name of product to retrieve
     * @return Optional containing product if found, empty otherwise
     */
    Optional<Product> getProductByName(String name);

    /**
     * Retrieves all active products with pagination support.
     * 
     * @param pageable pagination information
     * @return Page of active products
     */
    Page<Product> getAllActiveProducts(Pageable pageable);

    /**
     * Retrieves active products as DTOs for web layer.
     * 
     * @param pageable pagination information
     * @return Page of active product DTOs
     */
    Page<ProductDto> getActiveProductsDto(Pageable pageable);

    /**
     * Retrieves all products (including inactive) with pagination support.
     * 
     * @param pageable pagination information
     * @return Page of all products
     */
    Page<Product> getAllProducts(Pageable pageable);

    /**
     * Searches for products by name or description.
     * 
     * This method provides comprehensive search functionality
     * for finding products based on keywords.
     * 
     * @param keyword search keyword
     * @param pageable pagination information
     * @return Page of products matching search criteria
     */
    Page<Product> searchProducts(String keyword, Pageable pageable);

    /**
     * Searches for products by name or description as DTOs for web layer.
     * 
     * This method provides comprehensive search functionality
     * for finding products based on keywords, returning DTOs.
     * 
     * @param keyword search keyword
     * @param pageable pagination information
     * @return Page of product DTOs matching search criteria
     */
    Page<ProductDto> searchProductsDto(String keyword, Pageable pageable);

    /**
     * Finds products within a specified price range.
     * 
     * @param minPrice minimum price (inclusive)
     * @param maxPrice maximum price (inclusive)
     * @param pageable pagination information
     * @return Page of products within price range
     */
    Page<Product> findProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * Finds products with price less than or equal to specified amount.
     * 
     * @param maxPrice maximum price
     * @param pageable pagination information
     * @return Page of products with price <= maxPrice
     */
    Page<Product> findProductsByMaxPrice(BigDecimal maxPrice, Pageable pageable);

    /**
     * Finds products with price greater than or equal to specified amount.
     * 
     * @param minPrice minimum price
     * @param pageable pagination information
     * @return Page of products with price >= minPrice
     */
    Page<Product> findProductsByMinPrice(BigDecimal minPrice, Pageable pageable);

    /**
     * Retrieves products that are currently in stock.
     * 
     * @param pageable pagination information
     * @return Page of products with stock > 0 and active status
     */
    Page<Product> getInStockProducts(Pageable pageable);

    /**
     * Retrieves products that are out of stock.
     * 
     * @return List of products with stock <= 0
     */
    List<Product> getOutOfStockProducts();

    /**
     * Retrieves products with low stock (below threshold).
     * 
     * @param threshold stock quantity threshold
     * @return List of products with stock < threshold
     */
    List<Product> getLowStockProducts(Integer threshold);

    /**
     * Retrieves recently added products.
     * 
     * @param limit maximum number of products to return
     * @return List of recently added products
     */
    List<Product> getRecentProducts(Integer limit);

    /**
     * Activates or deactivates a product.
     * 
     * This method is used for product management to
     * control product visibility and availability.
     * 
     * @param id     ID of product
     * @param active true to activate, false to deactivate
     * @return updated product
     * @throws RuntimeException if product not found
     */
    Product setProductActive(Long id, boolean active);

    /**
     * Restocks a product by increasing its quantity.
     * 
     * This method handles inventory restocking with proper
     * validation and business rule enforcement.
     * 
     * @param id       ID of product to restock
     * @param quantity quantity to add
     * @return updated product
     * @throws IllegalArgumentException if quantity is not positive
     * @throws RuntimeException if product not found
     */
    Product restockProduct(Long id, Integer quantity);

    /**
     * Reduces product stock quantity.
     * 
     * This method is used during order processing to
     * update inventory levels.
     * 
     * @param id       ID of product
     * @param quantity quantity to reduce
     * @return updated product
     * @throws IllegalArgumentException if quantity is not positive
     * @throws RuntimeException if insufficient stock or product not found
     */
    Product reduceStock(Long id, Integer quantity);

    /**
     * Checks if a product name is available.
     * 
     * @param name product name to check
     * @return true if available, false if already exists
     */
    boolean isProductNameAvailable(String name);

    /**
     * Validates product data for creation or update.
     * 
     * This method performs comprehensive validation of product data
     * according to business rules and constraints.
     * 
     * @param product product to validate
     * @return true if valid, false otherwise
     */
    boolean validateProductData(Product product);

    /**
     * Retrieves products sorted by price (ascending).
     * 
     * @param pageable pagination information
     * @return Page of products sorted by price (low to high)
     */
    Page<Product> getProductsSortedByPriceAsc(Pageable pageable);

    /**
     * Retrieves products sorted by price (descending).
     * 
     * @param pageable pagination information
     * @return Page of products sorted by price (high to low)
     */
    Page<Product> getProductsSortedByPriceDesc(Pageable pageable);

    /**
     * Retrieves total number of products in the system.
     * 
     * @return total count of products
     */
    long getTotalProductCount();

    /**
     * Retrieves number of active products.
     * 
     * @return count of active products
     */
    long getActiveProductCount();

    /**
     * Retrieves number of out of stock products.
     * 
     * @return count of products with stock <= 0
     */
    long getOutOfStockProductCount();

    /**
     * Calculates total value of all products in inventory.
     * 
     * @return total inventory value (price × stock for all products)
     */
    BigDecimal getTotalInventoryValue();

    /**
     * Finds products by category (if category field is added).
     * 
     * @param category category to filter by
     * @param pageable pagination information
     * @return Page of products in specified category
     */
    Page<Product> findProductsByCategory(String category, Pageable pageable);

    /**
     * Updates product image URL.
     * 
     * This method handles product image updates
     * for product management features.
     * 
     * @param id        ID of product
     * @param imageUrl new image URL
     * @return updated product
     * @throws RuntimeException if product not found
     */
    Product updateProductImage(Long id, String imageUrl);

    /**
     * Bulk creates multiple products.
     * 
     * This method is used for importing products
     * from external sources or bulk operations.
     * 
     * @param products list of products to create
     * @return list of created products
     * @throws IllegalArgumentException if any product data is invalid
     */
    List<Product> createProductsBulk(List<Product> products);

    /**
     * Bulk updates multiple products.
     * 
     * This method is used for bulk price updates
     * or inventory management operations.
     * 
     * @param products list of products with updated data
     * @return list of updated products
     * @throws RuntimeException if any product not found
     */
    List<Product> updateProductsBulk(List<Product> products);
}
