package com.vti.bevtilib.service;

import com.vti.bevtilib.dto.OrderDTO;
import com.vti.bevtilib.dto.OrderItemDTO;
import com.vti.bevtilib.dto.OrderRequestDTO;
import com.vti.bevtilib.model.*;
import com.vti.bevtilib.repository.CartItemRepository;
import com.vti.bevtilib.repository.OrderRepository;
import com.vti.bevtilib.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional
    public OrderDTO createOrder(User user, OrderRequestDTO request) throws Exception {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new Exception("Vui lòng chọn ít nhất một sản phẩm để đặt hàng.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        order.setPhone(request.getPhone());
        order.setNote(request.getNote());
        order.setStatus(OrderStatus.PENDING);

        if (request.getPaymentMethod() != null) {
            order.setPaymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()));
        } else {
            order.setPaymentMethod(PaymentMethod.COD);
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderRequestDTO.OrderItemRequestDTO itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new Exception("Không tìm thấy sản phẩm với ID: " + itemReq.getProductId()));

            if (product.getStockQuantity() < itemReq.getQuantity()) {
                throw new Exception("Sản phẩm '" + product.getName() + "' chỉ còn " + product.getStockQuantity() + " trong kho.");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setPrice(product.getPrice());
            order.addOrderItem(orderItem);

            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));

            product.setStockQuantity(product.getStockQuantity() - itemReq.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        // Xóa giỏ hàng sau khi đặt hàng thành công
        cartItemRepository.deleteByUser(user);

        return convertToDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersForUser(User user) {
        return orderRepository.findByUser_UserIdOrderByOrderDateDesc(user.getUserId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) throws Exception {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new Exception("Không tìm thấy đơn hàng với ID: " + id));
        return convertToDto(order);
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long id, String status) throws Exception {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new Exception("Không tìm thấy đơn hàng với ID: " + id));

        OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());

        // Nếu hủy đơn hàng, trả lại số lượng sản phẩm
        if (newStatus == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.CANCELLED) {
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(newStatus);
        return convertToDto(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(String status, Pageable pageable) {
        Page<Order> orders;
        if (status != null && !status.isEmpty()) {
            orders = orderRepository.findByStatusOrderByOrderDateDesc(OrderStatus.valueOf(status.toUpperCase()), pageable);
        } else {
            orders = orderRepository.findAllByOrderByOrderDateDesc(pageable);
        }
        return orders.map(this::convertToDto);
    }

    private OrderDTO convertToDto(Order order) {
        List<OrderItemDTO> items = order.getOrderItems().stream()
                .map(item -> OrderItemDTO.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productImage(item.getProduct().getCoverImage())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderDTO.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .phone(order.getPhone())
                .note(order.getNote())
                .paymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod().name() : null)
                .status(order.getStatus().name())
                .items(items)
                .build();
    }
}
