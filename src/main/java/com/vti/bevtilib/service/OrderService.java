package com.vti.bevtilib.service;

import com.vti.bevtilib.dto.OrderDTO;
import com.vti.bevtilib.dto.OrderRequestDTO;
import com.vti.bevtilib.model.Order;
import com.vti.bevtilib.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderDTO createOrder(User user, OrderRequestDTO request) throws Exception;
    List<OrderDTO> getOrdersForUser(User user);
    OrderDTO getOrderById(Long id) throws Exception;
    OrderDTO updateOrderStatus(Long id, String status) throws Exception;
    Page<OrderDTO> getAllOrders(String status, Pageable pageable);
}
