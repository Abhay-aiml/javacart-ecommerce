package com.valo.ecommerce.controller;

import com.valo.ecommerce.model.Category;
import com.valo.ecommerce.model.OrderStatus;
import com.valo.ecommerce.model.Product;
import com.valo.ecommerce.repository.CategoryRepository;
import com.valo.ecommerce.repository.ProductRepository;
import com.valo.ecommerce.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderService orderService;

    public AdminController(ProductRepository productRepository, CategoryRepository categoryRepository,
                            OrderService orderService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.orderService = orderService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("productCount", productRepository.count());
        model.addAttribute("orderCount", orderService.getAllOrders().size());
        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "admin/products";
    }

    @GetMapping("/products/new")
    public String newProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/product-form";
    }

    @GetMapping("/products/{id}/edit")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@RequestParam(required = false) Long id,
                               @RequestParam String name,
                               @RequestParam String description,
                               @RequestParam BigDecimal price,
                               @RequestParam Integer stockQuantity,
                               @RequestParam(required = false) String imageUrl,
                               @RequestParam(required = false) String categoryId,
                               @RequestParam(required = false) String active) {
        Product product = (id != null)
                ? productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"))
                : new Product();

        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        product.setImageUrl(imageUrl);
        // Unchecked checkboxes simply aren't submitted at all, so absence means false.
        product.setActive("true".equals(active));

        // The "— None —" option submits an empty string, which can't convert to
        // a Long — parse it ourselves instead of binding it as a request param.
        if (categoryId != null && !categoryId.isBlank()) {
            Category category = categoryRepository.findById(Long.valueOf(categoryId)).orElse(null);
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }

        productRepository.save(product);
        return "redirect:/admin/products";
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "redirect:/admin/products";
    }

    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("newCategory", new Category());
        return "admin/categories";
    }

    @PostMapping("/categories/save")
    public String saveCategory(@RequestParam String name) {
        categoryRepository.save(new Category(name));
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return "redirect:/admin/categories";
    }

    @GetMapping("/orders")
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        orderService.updateStatus(id, status);
        return "redirect:/admin/orders";
    }
}
