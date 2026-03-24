package com.vti.bevtilib.repository;

import com.vti.bevtilib.model.Order;
import com.vti.bevtilib.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser_UserIdOrderByOrderDateDesc(String userId);
    Page<Order> findAllByOrderByOrderDateDesc(Pageable pageable);
    Page<Order> findByStatusOrderByOrderDateDesc(OrderStatus status, Pageable pageable);
}
