package com.valo.ecommerce.service;

import com.valo.ecommerce.model.*;
import com.valo.ecommerce.repository.CartItemRepository;
import com.valo.ecommerce.repository.OrderRepository;
import com.valo.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, CartItemRepository cartItemRepository,
                         ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order placeOrder(User user, String shippingAddress) {
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Your cart is empty.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : cartItems) {
            Product product = ci.getProduct();
            if (ci.getQuantity() > product.getStockQuantity()) {
                throw new IllegalStateException("Not enough stock for " + product.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setPriceAtPurchase(product.getPrice());
            orderItem.setQuantity(ci.getQuantity());
            order.addItem(orderItem);

            total = total.add(orderItem.getSubtotal());

            product.setStockQuantity(product.getStockQuantity() - ci.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);

        cartItemRepository.deleteByUser(user);

        return saved;
    }

    public List<Order> getOrdersForUser(User user) {
        return orderRepository.findByUserOrderByPlacedAtDesc(user);
    }

    /**
     * Fetches an order but only returns it if it belongs to the given user
     * (or the user is an admin) — prevents one customer from viewing another
     * customer's order by guessing/incrementing the id in the URL.
     */
    public Order getOrderForUser(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        boolean isOwner = order.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRoles().contains("ADMIN");
        if (!isOwner && !isAdmin) {
            throw new IllegalArgumentException("Order not found");
        }
        return order;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByPlacedAtDesc();
    }

    @Transactional
    public void updateStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }
}
