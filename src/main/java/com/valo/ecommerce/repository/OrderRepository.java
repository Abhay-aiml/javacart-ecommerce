package com.valo.ecommerce.repository;

import com.valo.ecommerce.model.Order;
import com.valo.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByPlacedAtDesc(User user);
    List<Order> findAllByOrderByPlacedAtDesc();
}
