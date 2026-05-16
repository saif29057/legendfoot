package com.ecommerce.app.controller;

import com.ecommerce.app.dto.ProductDto;
import com.ecommerce.app.entity.Product;
import com.ecommerce.app.service.ProductService;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for handling product-related operations.
 * 
 * This controller follows SOLID principles:
 * - Single Responsibility: Handles only product-related web requests
 * - Open/Closed: Open for extension through additional endpoints
 * - Liskov Substitution: Can be substituted with any ProductController implementation
 * - Interface Segregation: Provides only product-related methods
 * - Dependency Inversion: Depends on ProductService interface, not concrete classes
 * 
 * The class provides a clean separation between web layer
 * and business logic, following best practices for controller design.
 */
@Controller
@RequestMapping("/products")
@Slf4j
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Displays list of all products.
     * 
     * This method shows all available products with pagination
     * and optional search functionality.
     * 
     * @param model for view attributes
     * @param pageable for pagination
     * @param search optional search term
     * @return name of products list view template
     */
    @GetMapping
    public String listProducts(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String search) {
        
        log.debug("Loading products list - page: {}, size: {}, search: {}", page, size, search);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products;
        
        if (search != null && !search.trim().isEmpty()) {
            products = productService.searchProductsDto(search, pageable);
        } else {
            products = productService.getActiveProductsDto(pageable);
        }
        
        model.addAttribute("products", products);
        model.addAttribute("search", search);
        model.addAttribute("pageTitle", "Products");
        model.addAttribute("currentPage", "products");
        
        return "products/list";
    }

    /**
     * Displays product details page.
     * 
     * This method shows detailed information about a specific product.
     * 
     * @param id product ID
     * @param model for view attributes
     * @return name of product details view template
     */
    @GetMapping("/{id}")
    public String showProduct(
            @PathVariable Long id,
            Model model) {
        
        log.debug("Loading product details for ID: {}", id);
        
        Optional<Product> productOpt = productService.getProductById(id);
        if (productOpt.isEmpty()) {
            log.warn("Product not found with ID: {}", id);
            return "redirect:/products?error=Product not found";
        }
        
        ProductDto product = ProductDto.fromEntity(productOpt.get());
        
        model.addAttribute("product", product);
        model.addAttribute("pageTitle", product.getName());
        model.addAttribute("currentPage", "products");
        
        return "products/details";
    }

    /**
     * Displays product creation form (Admin only).
     * 
     * This method shows form for creating new products.
     * Only accessible to users with ADMIN role.
     * 
     * @param model for view attributes
     * @return name of product creation view template
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        log.debug("Displaying product creation form");
        
        model.addAttribute("productDto", new ProductDto());
        model.addAttribute("pageTitle", "Create Product");
        model.addAttribute("currentPage", "products");
        
        return "products/create";
    }

    /**
     * Processes product creation (Admin only).
     * 
     * This method handles new product creation with validation
     * and provides appropriate feedback to the user.
     * Only accessible to users with ADMIN role.
     * 
     * @param productDto product creation data
     * @param bindingResult validation results
     * @param redirectAttributes for flash messages
     * @param model for view attributes
     * @return redirect to products list on success
     */
    @PostMapping("/new")
    public String createProduct(
            @Valid @ModelAttribute("productDto") ProductDto productDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        log.debug("Processing product creation for name: {}", productDto.getName());
        
        if (bindingResult.hasErrors()) {
            log.warn("Product creation validation failed for name: {}", productDto.getName());
            model.addAttribute("productDto", productDto);
            model.addAttribute("pageTitle", "Create Product");
            model.addAttribute("currentPage", "products");
            return "products/create";
        }
        
        try {
            productService.createProduct(productDto);
            log.info("Product created successfully: {}", productDto.getName());
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Product created successfully!");
            return "redirect:/products";
            
        } catch (Exception e) {
            log.error("Product creation failed for name: {}", productDto.getName(), e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Product creation failed: " + e.getMessage());
            return "redirect:/products/new";
        }
    }

    /**
     * Displays product edit form (Admin only).
     * 
     * This method shows form for editing existing products.
     * Only accessible to users with ADMIN role.
     * 
     * @param id product ID
     * @param model for view attributes
     * @return name of product edit view template
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(
            @PathVariable Long id,
            Model model) {
        
        log.debug("Displaying product edit form for ID: {}", id);
        
        Optional<Product> productOpt = productService.getProductById(id);
        if (productOpt.isEmpty()) {
            log.warn("Product not found for editing with ID: {}", id);
            return "redirect:/products?error=Product not found";
        }
        
        ProductDto product = ProductDto.fromEntity(productOpt.get());
        
        model.addAttribute("productDto", product);
        model.addAttribute("pageTitle", "Edit Product: " + product.getName());
        model.addAttribute("currentPage", "products");
        
        return "products/edit";
    }

    /**
     * Processes product update (Admin only).
     * 
     * This method handles product updates with validation
     * and provides appropriate feedback to the user.
     * Only accessible to users with ADMIN role.
     * 
     * @param id product ID
     * @param productDto product update data
     * @param bindingResult validation results
     * @param redirectAttributes for flash messages
     * @param model for view attributes
     * @return redirect to products list on success
     */
    @PostMapping("/{id}/edit")
    public String updateProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute("productDto") ProductDto productDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        log.debug("Processing product update for ID: {}, name: {}", id, productDto.getName());
        
        if (bindingResult.hasErrors()) {
            log.warn("Product update validation failed for ID: {}", id);
            model.addAttribute("productDto", productDto);
            model.addAttribute("pageTitle", "Edit Product: " + productDto.getName());
            model.addAttribute("currentPage", "products");
            return "products/edit";
        }
        
        try {
            productService.updateProduct(id, productDto);
            log.info("Product updated successfully: {}", productDto.getName());
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Product updated successfully!");
            return "redirect:/products";
            
        } catch (Exception e) {
            log.error("Product update failed for ID: {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Product update failed: " + e.getMessage());
            return "redirect:/products/" + id + "/edit";
        }
    }

    /**
     * Processes product deletion (Admin only).
     * 
     * This method handles product deletion with confirmation
     * and provides appropriate feedback to the user.
     * Only accessible to users with ADMIN role.
     * 
     * @param id product ID
     * @param redirectAttributes for flash messages
     * @return redirect to products list on success
     */
    @PostMapping("/{id}/delete")
    public String deleteProduct(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        log.debug("Processing product deletion for ID: {}", id);
        
        try {
            productService.deleteProduct(id);
            log.info("Product deleted successfully: {}", id);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Product deleted successfully!");
            return "redirect:/products";
            
        } catch (Exception e) {
            log.error("Product deletion failed for ID: {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Product deletion failed: " + e.getMessage());
            return "redirect:/products";
        }
    }
}
