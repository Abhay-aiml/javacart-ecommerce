package com.valo.ecommerce.config;

import com.valo.ecommerce.model.Category;
import com.valo.ecommerce.model.Product;
import com.valo.ecommerce.model.User;
import com.valo.ecommerce.repository.CategoryRepository;
import com.valo.ecommerce.repository.ProductRepository;
import com.valo.ecommerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

/**
 * Populates the in-memory H2 database with demo data on every startup, so the
 * site is browsable immediately. Swap this out (or guard it behind a profile)
 * once you move to a persistent database.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(CategoryRepository categoryRepository, ProductRepository productRepository,
                       UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) return;

        Category electronics = categoryRepository.save(new Category("Electronics"));
        Category books = categoryRepository.save(new Category("Books"));
        Category fashion = categoryRepository.save(new Category("Fashion"));
        Category home = categoryRepository.save(new Category("Home & Kitchen"));

        productRepository.saveAll(List.of(
            product("Wireless Mechanical Keyboard", "Hot-swappable switches, RGB backlight, USB-C.", "2499.00", 40, electronics),
            product("Noise Cancelling Headphones", "Over-ear, 30-hour battery, USB-C fast charge.", "4999.00", 25, electronics),
            product("27-inch 4K Monitor", "IPS panel, 144Hz, HDR400.", "24999.00", 12, electronics),
            product("Portable SSD 1TB", "USB 3.2 Gen 2, up to 1050MB/s read.", "6999.00", 30, electronics),
            product("Clean Code", "A Handbook of Agile Software Craftsmanship.", "699.00", 50, books),
            product("Effective Java", "Best practices for the Java platform, 3rd Edition.", "899.00", 35, books),
            product("Designing Data-Intensive Applications", "The big ideas behind reliable, scalable systems.", "1199.00", 20, books),
            product("Cotton Graphic T-Shirt", "Soft cotton tee, regular fit.", "599.00", 100, fashion),
            product("Denim Jacket", "Classic fit denim jacket, all seasons.", "1899.00", 45, fashion),
            product("Running Shoes", "Lightweight mesh upper, cushioned sole.", "2999.00", 60, fashion),
            product("Non-Stick Cookware Set", "5-piece set, induction compatible.", "3499.00", 18, home),
            product("Electric Kettle", "1.7L, auto shut-off, stainless steel body.", "1299.00", 40, home)
        ));

        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Store Admin");
            admin.setRoles(new HashSet<>(List.of("ADMIN", "CUSTOMER")));
            userRepository.save(admin);
        }
    }

    private Product product(String name, String description, String price, int stock, Category category) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setPrice(new BigDecimal(price));
        p.setStockQuantity(stock);
        p.setCategory(category);
        p.setActive(true);
        return p;
    }
}
