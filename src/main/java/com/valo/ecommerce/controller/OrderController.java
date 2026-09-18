package com.valo.ecommerce.controller;

import com.valo.ecommerce.model.Order;
import com.valo.ecommerce.service.AppUserDetails;
import com.valo.ecommerce.service.CartService;
import com.valo.ecommerce.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;

    public OrderController(OrderService orderService, CartService cartService) {
        this.orderService = orderService;
        this.cartService = cartService;
    }

    @GetMapping("/checkout")
    public String checkoutPage(@AuthenticationPrincipal AppUserDetails principal, Model model) {
        model.addAttribute("cartItems", cartService.getCart(principal.getUser()));
        model.addAttribute("cartTotal", cartService.getCartTotal(principal.getUser()));
        model.addAttribute("defaultAddress", principal.getUser().getAddress());
        return "checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@RequestParam String shippingAddress,
                              @AuthenticationPrincipal AppUserDetails principal,
                              Model model) {
        try {
            Order order = orderService.placeOrder(principal.getUser(), shippingAddress);
            return "redirect:/orders/" + order.getId() + "/confirmation";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("cartItems", cartService.getCart(principal.getUser()));
            model.addAttribute("cartTotal", cartService.getCartTotal(principal.getUser()));
            return "checkout";
        }
    }

    @GetMapping("/orders/{id}/confirmation")
    public String confirmation(@PathVariable Long id, @AuthenticationPrincipal AppUserDetails principal, Model model) {
        model.addAttribute("order", orderService.getOrderForUser(id, principal.getUser()));
        return "order-confirmation";
    }

    @GetMapping("/orders")
    public String myOrders(@AuthenticationPrincipal AppUserDetails principal, Model model) {
        model.addAttribute("orders", orderService.getOrdersForUser(principal.getUser()));
        return "orders";
    }
}
