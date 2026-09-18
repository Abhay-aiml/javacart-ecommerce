package com.valo.ecommerce.controller;

import com.valo.ecommerce.model.Category;
import com.valo.ecommerce.model.Product;
import com.valo.ecommerce.repository.CategoryRepository;
import com.valo.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductController(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/")
    public String home(@RequestParam(required = false) Long categoryId, Model model) {
        List<Product> products = categoryId == null
                ? productRepository.findByActiveTrue()
                : productRepository.findByCategoryIdAndActiveTrue(categoryId);
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("selectedCategoryId", categoryId);
        return "home";
    }

    @GetMapping("/search")
    public String search(@RequestParam String q, Model model) {
        model.addAttribute("products", productRepository.search(q));
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("query", q);
        return "search-results";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        model.addAttribute("product", product);
        return "product-detail";
    }
}
