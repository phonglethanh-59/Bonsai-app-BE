package com.vti.bevtilib.service;

import com.vti.bevtilib.dto.OrderDTO;
import com.vti.bevtilib.dto.OrderItemDTO;
import com.vti.bevtilib.dto.OrderRequestDTO;
import com.vti.bevtilib.exception.AccessDeniedException;
import com.vti.bevtilib.exception.BusinessException;
import com.vti.bevtilib.exception.ResourceNotFoundException;
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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    // Bảng chuyển trạng thái hợp lệ
    private static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS;

    static {
        VALID_TRANSITIONS = new EnumMap<>(OrderStatus.class);
        VALID_TRANSITIONS.put(OrderStatus.PENDING, Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED));
        VALID_TRANSITIONS.put(OrderStatus.CONFIRMED, Set.of(OrderStatus.SHIPPING, OrderStatus.CANCELLED));
        VALID_TRANSITIONS.put(OrderStatus.SHIPPING, Set.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED));
        VALID_TRANSITIONS.put(OrderStatus.DELIVERED, Set.of()); // Trạng thái cuối
        VALID_TRANSITIONS.put(OrderStatus.CANCELLED, Set.of()); // Trạng thái cuối
    }

    @Override
    @Transactional
    public OrderDTO createOrder(User user, OrderRequestDTO request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessException("Vui lòng chọn ít nhất một sản phẩm để đặt hàng.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        order.setPhone(request.getPhone());
        order.setNote(request.getNote());
        order.setStatus(OrderStatus.PENDING);

        if (request.getPaymentMethod() != null) {
            try {
                order.setPaymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()));
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Phương thức thanh toán không hợp lệ: " + request.getPaymentMethod());
            }
        } else {
            order.setPaymentMethod(PaymentMethod.COD);
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        // Lưu danh sách product ID đã đặt để chỉ xóa đúng items trong giỏ
        List<Long> orderedProductIds = request.getItems().stream()
                .map(OrderRequestDTO.OrderItemRequestDTO::getProductId)
                .collect(Collectors.toList());

        for (OrderRequestDTO.OrderItemRequestDTO itemReq : request.getItems()) {
            if (itemReq.getQuantity() <= 0) {
                throw new BusinessException("Số lượng sản phẩm phải lớn hơn 0.");
            }

            // Pessimistic lock để tránh race condition khi 2 người đặt cùng lúc
            Product product = productRepository.findByIdForUpdate(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + itemReq.getProductId()));

            if (product.getStockQuantity() < itemReq.getQuantity()) {
                throw new BusinessException("Sản phẩm '" + product.getName() + "' chỉ còn " + product.getStockQuantity() + " trong kho.");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setPrice(product.getPrice());
            order.addOrderItem(orderItem);

            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));

            product.setStockQuantity(product.getStockQuantity() - itemReq.getQuantity());
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        // Chỉ xóa các sản phẩm đã đặt khỏi giỏ hàng (không xóa toàn bộ giỏ)
        cartItemRepository.deleteByUserAndProduct_IdIn(user, orderedProductIds);

        return convertToDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersForUser(User user) {
        return orderRepository.findByUserWithItems(user.getUserId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersForUser(User user, String status, Pageable pageable) {
        Page<Order> orders;
        if (status != null && !status.isEmpty()) {
            try {
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                orders = orderRepository.findByUserAndStatusWithItems(user.getUserId(), orderStatus, pageable);
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Trạng thái đơn hàng không hợp lệ: " + status);
            }
        } else {
            orders = orderRepository.findByUserWithItems(user.getUserId(), pageable);
        }
        return orders.map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id, User user) {
        Order order = orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + id));

        // Kiểm tra quyền: chỉ chủ đơn hàng hoặc ADMIN/STAFF mới được xem
        if (!order.getUser().getUserId().equals(user.getUserId())
                && !"ADMIN".equalsIgnoreCase(user.getRole())
                && !"STAFF".equalsIgnoreCase(user.getRole())) {
            throw new AccessDeniedException("Bạn không có quyền xem đơn hàng này.");
        }

        return convertToDto(order);
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + id));

        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Trạng thái đơn hàng không hợp lệ: " + status);
        }

        // Kiểm tra chuyển trạng thái hợp lệ
        OrderStatus currentStatus = order.getStatus();
        Set<OrderStatus> allowedNextStatuses = VALID_TRANSITIONS.getOrDefault(currentStatus, Set.of());
        if (!allowedNextStatuses.contains(newStatus)) {
            throw new BusinessException("Không thể chuyển trạng thái từ " + currentStatus + " sang " + newStatus
                    + ". Trạng thái hợp lệ tiếp theo: " + allowedNextStatuses);
        }

        // Nếu hủy đơn hàng, trả lại số lượng sản phẩm
        if (newStatus == OrderStatus.CANCELLED) {
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
            try {
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                orders = orderRepository.findByStatusWithItems(orderStatus, pageable);
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Trạng thái đơn hàng không hợp lệ: " + status);
            }
        } else {
            orders = orderRepository.findAllWithItems(pageable);
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
