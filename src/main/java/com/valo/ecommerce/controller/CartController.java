package com.valo.ecommerce.controller;

import com.valo.ecommerce.model.Product;
import com.valo.ecommerce.repository.ProductRepository;
import com.valo.ecommerce.service.AppUserDetails;
import com.valo.ecommerce.service.CartService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final ProductRepository productRepository;

    public CartController(CartService cartService, ProductRepository productRepository) {
        this.cartService = cartService;
        this.productRepository = productRepository;
    }

    @GetMapping
    public String viewCart(@AuthenticationPrincipal AppUserDetails principal, Model model) {
        model.addAttribute("cartItems", cartService.getCart(principal.getUser()));
        model.addAttribute("cartTotal", cartService.getCartTotal(principal.getUser()));
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, @RequestParam(defaultValue = "1") int quantity,
                             @AuthenticationPrincipal AppUserDetails principal) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        cartService.addToCart(principal.getUser(), product, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long cartItemId, @RequestParam int quantity) {
        cartService.updateQuantity(cartItemId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeItem(@RequestParam Long cartItemId) {
        cartService.removeItem(cartItemId);
        return "redirect:/cart";
    }
}
