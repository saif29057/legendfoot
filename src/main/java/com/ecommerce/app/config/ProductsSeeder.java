package com.ecommerce.app.config;

import com.ecommerce.app.entity.Product;
import com.ecommerce.app.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

/**
 * Seeds the products table with default football products on first startup.
 *
 * Mirrors the pattern used by AdminUserInitializer: runs once as a
 * CommandLineRunner and only inserts data when the products table is empty,
 * so it is safe to run on every restart without creating duplicates.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class ProductsSeeder {

    private final ProductRepository productRepository;

    @Bean
    CommandLineRunner seedProducts() {
        return args -> {
            if (productRepository.count() == 0) {
                seedInitialProducts();
            } else {
                // Products already exist; clear any stale local image paths that
                // point to files which were never uploaded to the static folder.
                clearBrokenLocalImageUrls();
            }
        };
    }

    /**
     * Nulls out image URLs that begin with "/images/products/" because those
     * static files do not exist on disk. The Thymeleaf template already shows
     * a graceful text placeholder when imageUrl is null, so this is safe.
     */
    private void clearBrokenLocalImageUrls() {
        List<Product> broken = productRepository.findAll().stream()
                .filter(p -> p.getImageUrl() != null
                        && p.getImageUrl().startsWith("/images/products/"))
                .peek(p -> p.setImageUrl(null))
                .toList();

        if (!broken.isEmpty()) {
            productRepository.saveAll(broken);
            log.info("Cleared {} broken local image URL(s) from existing products.",
                    broken.size());
        }
    }

    private void seedInitialProducts() {
        List<Product> products = List.of(

            // Boots
            build("Nike Mercurial Superfly 9 Elite",
                  "Elite-speed football boots with a Dynamic Fit collar, Flyknit upper and All Conditions Control rubber for grip in any weather.",
                  new BigDecimal("249.99"), 40),

            build("Adidas Predator Accuracy+ FG",
                  "Pro-level boots with multi-zone Hybrid touch elements and a sock-style construction for a locked-in feel.",
                  new BigDecimal("229.99"), 35),

            build("Puma King Platinum FG/AG",
                  "The iconic K-leather upper in a modern silhouette engineered for touch, comfort and durability.",
                  new BigDecimal("189.99"), 50),

            build("Nike Phantom GX Elite FG",
                  "Gripknit upper provides unmatched first-touch grip so you can receive and release with confidence.",
                  new BigDecimal("269.99"), 25),

            build("Adidas Copa Pure II+ FG",
                  "Premium leather boots crafted for pure touch, with a sock tongue and 2D Controlframe outsole for stability.",
                  new BigDecimal("199.99"), 45),

            build("New Balance Furon v7+ Pro FG",
                  "Lightweight speed boots with a 3D-molded HypoKnit upper that flexes and stretches with every movement.",
                  new BigDecimal("179.99"), 30),

            // Match Balls
            build("Nike Flight Premier League Ball",
                  "Official match ball of the Premier League. Aerowtrac grooves and asymmetrical wing design for true, consistent flight.",
                  new BigDecimal("34.99"), 120),

            build("Adidas UCL Pro Ball 2024",
                  "Official UEFA Champions League match ball. Thermally bonded seamless panels for superior aerodynamics and touch.",
                  new BigDecimal("39.99"), 100),

            build("Nike Strike Football Size 5",
                  "Durable training ball with high-contrast graphics and reinforced rubber bladder, perfect for everyday use.",
                  new BigDecimal("24.99"), 200),

            // Goalkeeper Gloves
            build("Reusch Attrakt Gold GK Gloves",
                  "Top-tier goalkeeper gloves with Ortho-Tec finger protection, Evolution Negative Cut and Ultimate Contact foam.",
                  new BigDecimal("89.99"), 60),

            build("Adidas Predator Pro GK Gloves",
                  "Professional gloves with Hybrid Cut, contact zones on the backhand and a pre-curved design for natural positioning.",
                  new BigDecimal("79.99"), 70),

            // Jerseys
            build("FC Barcelona 2024/25 Home Jersey",
                  "Official home shirt with Dri-FIT ADV technology, recycled polyester fabric and the iconic blaugrana stripes.",
                  new BigDecimal("119.99"), 80),

            build("Real Madrid 2024/25 Home Jersey",
                  "Official home shirt with Dri-FIT ADV technology - lightweight, breathable and built for elite performance.",
                  new BigDecimal("119.99"), 80),

            build("Manchester City 2024/25 Away Jersey",
                  "Lightweight Dri-FIT away kit celebrating the club's sky-blue heritage with a modern geometric pattern.",
                  new BigDecimal("109.99"), 65),

            // Training & Accessories
            build("Nike Pro Shin Guards",
                  "Lightweight hard-shell shin guards with a breathable ankle sleeve to keep you protected without slowing you down.",
                  new BigDecimal("29.99"), 150),

            build("Adidas Training Cone Set 20pcs",
                  "Durable disc cones ideal for dribbling drills, speed ladders and pitch marking in any weather condition.",
                  new BigDecimal("19.99"), 300),

            build("Precision Football Rebounder Net",
                  "Multi-angle passing and shooting rebounder that lets you train your first touch and shooting technique solo.",
                  new BigDecimal("59.99"), 55)
        );

        productRepository.saveAll(products);
        log.info("Seeded {} football products into the database.", products.size());
    }

    private Product build(String name, String description, BigDecimal price, int stock) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setStockQuantity(stock);
        p.setImageUrl(null); // no static images uploaded; template shows a styled text placeholder
        p.setActive(true);
        return p;
    }
}
