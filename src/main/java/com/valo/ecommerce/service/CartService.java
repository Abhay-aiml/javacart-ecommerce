package com.valo.ecommerce.service;

import com.valo.ecommerce.model.CartItem;
import com.valo.ecommerce.model.Product;
import com.valo.ecommerce.model.User;
import com.valo.ecommerce.repository.CartItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;

    public CartService(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    public List<CartItem> getCart(User user) {
        return cartItemRepository.findByUser(user);
    }

    @Transactional
    public void addToCart(User user, Product product, int quantity) {
        CartItem item = cartItemRepository.findByUserAndProduct(user, product)
                .orElse(new CartItem(user, product, 0));
        int newQty = item.getQuantity() == null ? quantity : item.getQuantity() + quantity;
        // Never let the cart hold more than what's in stock.
        item.setQuantity(Math.min(newQty, product.getStockQuantity()));
        cartItemRepository.save(item);
    }

    @Transactional
    public void updateQuantity(Long cartItemId, int quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(Math.min(quantity, item.getProduct().getStockQuantity()));
            cartItemRepository.save(item);
        }
    }

    @Transactional
    public void removeItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    @Transactional
    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }

    public BigDecimal getCartTotal(User user) {
        return getCart(user).stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getCartItemCount(User user) {
        return getCart(user).stream().mapToInt(CartItem::getQuantity).sum();
    }
}
