package com.valo.ecommerce.controller;

import com.valo.ecommerce.service.AppUserDetails;
import com.valo.ecommerce.service.CartService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Makes the logged-in user's cart item count available to every template
 * (used for the little badge next to "Cart" in the nav bar) without every
 * controller having to fetch and add it manually.
 */
@ControllerAdvice
public class GlobalModelAdvice {

    private final CartService cartService;

    public GlobalModelAdvice(CartService cartService) {
        this.cartService = cartService;
    }

    @ModelAttribute("cartCount")
    public int cartCount(@AuthenticationPrincipal AppUserDetails principal) {
        if (principal == null) return 0;
        return cartService.getCartItemCount(principal.getUser());
    }
}
