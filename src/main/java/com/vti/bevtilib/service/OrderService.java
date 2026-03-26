package com.vti.bevtilib.service;

import com.vti.bevtilib.dto.OrderDTO;
import com.vti.bevtilib.dto.OrderRequestDTO;
import com.vti.bevtilib.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderDTO createOrder(User user, OrderRequestDTO request);
    List<OrderDTO> getOrdersForUser(User user);
    Page<OrderDTO> getOrdersForUser(User user, String status, Pageable pageable);
    OrderDTO getOrderById(Long id, User user);
    OrderDTO updateOrderStatus(Long id, String status);
    Page<OrderDTO> getAllOrders(String status, Pageable pageable);
}
