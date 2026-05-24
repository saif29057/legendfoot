package com.ecommerce.app.controller;

import com.ecommerce.app.dto.ProductDto;
import com.ecommerce.app.dto.UserDto;
import com.ecommerce.app.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for handling home page and general requests.
 *
 * This controller follows SOLID principles:
 * - Single Responsibility: Handles only home page requests
 * - Open/Closed: Open for extension through additional endpoints
 * - Liskov Substitution: Can be substituted with any HomeController
 * implementation
 * - Interface Segregation: Provides only home page related methods
 * - Dependency Inversion: Depends on Spring MVC components
 *
 * The class provides a clean separation between web layer
 * and business logic, following best practices for controller design.
 */
@Controller
@Slf4j
public class HomeController {

    private final ProductService productService;

    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Handles requests to the home page.
     *
     * This method displays the main landing page with
     * featured products and navigation options.
     *
     * @return the name of the home view template
     */
    @GetMapping("/")
    public String home(Model model) {
        log.debug("Loading home page");

        List<ProductDto> featuredProducts = productService.getRecentProducts(4)
                .stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());

        model.addAttribute("featuredProducts", featuredProducts);
        model.addAttribute("pageTitle", "Welcome to LegendFoot");
        model.addAttribute("currentPage", "home");

        return "home";
    }

    /**
     * Handles requests to the /home alias.
     *
     * Spring Security redirects successful logins to /home, so this
     * endpoint must render the same landing page as /.
     */
    @GetMapping("/home")
    public String homeAlias(Model model) {
        return home(model);
    }

    /**
     * Handles requests to the about page.
     *
     * This method displays information about the e-commerce platform.
     *
     * @return the name of the about view template
     */
    @GetMapping("/about")
    public String about(Model model) {
        log.debug("Loading about page");

        model.addAttribute("pageTitle", "About E-Commerce");
        model.addAttribute("currentPage", "about");

        return "about";
    }

    /**
     * Handles requests to the contact page.
     *
     * This method displays contact information and form.
     *
     * @return the name of the contact view template
     */
    @GetMapping("/contact")
    public String contact(Model model) {
        log.debug("Loading contact page");

        model.addAttribute("pageTitle", "Contact Us");
        model.addAttribute("currentPage", "contact");

        return "contact";
    }

    /**
     * Handles requests to the access denied page.
     *
     * Spring Security forwards unauthorized role access here, so the
     * application needs a concrete view instead of a missing route.
     */
    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        log.debug("Loading access denied page");

        model.addAttribute("pageTitle", "Access Denied");
        model.addAttribute("currentPage", "access-denied");

        return "access-denied";
    }

    /**
     * Handles requests to the public registration page.
     *
     * The form posts to the existing /users/register handler so the
     * application keeps its current registration workflow.
     */
    @GetMapping("/register")
    public String register(Model model) {
        log.debug("Loading registration page");

        model.addAttribute("userDto", new UserDto());
        model.addAttribute("pageTitle", "Register");
        model.addAttribute("currentPage", "register");

        return "users/register";
    }

    /**
     * Handles requests to the login page.
     *
     * Spring Security is configured to use /login as the custom login page,
     * so this endpoint must return a view instead of falling through to a
     * static resource lookup.
     */
    @GetMapping("/login")
    public String login(Model model) {
        log.debug("Loading login page");

        model.addAttribute("pageTitle", "Login");
        model.addAttribute("currentPage", "login");

        return "users/login";
    }
}
