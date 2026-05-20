package com.vti.bevtilib.repository;

import com.vti.bevtilib.model.Order;
import com.vti.bevtilib.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // ========== Revenue queries ==========

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'DELIVERED' AND o.orderDate BETWEEN :from AND :to")
    java.math.BigDecimal sumRevenueByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status AND o.orderDate BETWEEN :from AND :to")
    long countByStatusAndDateRange(@Param("status") OrderStatus status, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate BETWEEN :from AND :to")
    long countByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value = "SELECT DATE(o.order_date) as d, COALESCE(SUM(o.total_amount), 0), COUNT(o.id) FROM orders o WHERE o.status = 'DELIVERED' AND o.order_date BETWEEN :from AND :to GROUP BY DATE(o.order_date) ORDER BY d", nativeQuery = true)
    List<Object[]> findDailyRevenue(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value = "SELECT p.name, SUM(oi.quantity), SUM(oi.price * oi.quantity) FROM order_items oi JOIN orders o ON oi.order_id = o.id JOIN products p ON oi.product_id = p.id WHERE o.status = 'DELIVERED' AND o.order_date BETWEEN :from AND :to GROUP BY p.name ORDER BY SUM(oi.price * oi.quantity) DESC", nativeQuery = true)
    List<Object[]> findTopProductsByRevenue(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product LEFT JOIN FETCH o.user u LEFT JOIN FETCH u.userDetail WHERE o.orderDate BETWEEN :from AND :to ORDER BY o.orderDate DESC")
    List<Order> findAllByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product LEFT JOIN FETCH o.user u LEFT JOIN FETCH u.userDetail WHERE o.status = :status AND o.orderDate BETWEEN :from AND :to ORDER BY o.orderDate DESC")
    List<Order> findAllByStatusAndDateRange(@Param("status") OrderStatus status, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product LEFT JOIN FETCH o.user u LEFT JOIN FETCH u.userDetail ORDER BY o.orderDate DESC")
    List<Order> findAllWithItemsAndUser();

    // ========== Dashboard stats queries ==========

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'DELIVERED'")
    java.math.BigDecimal sumTotalRevenue();

    @Query("SELECT COUNT(o) FROM Order o")
    long countAllOrders();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") OrderStatus status);

    // Monthly revenue (last 12 months)
    @Query(value = "SELECT YEAR(o.order_date), MONTH(o.order_date), COALESCE(SUM(o.total_amount), 0), COUNT(o.id) FROM orders o WHERE o.status = 'DELIVERED' AND o.order_date >= :since GROUP BY YEAR(o.order_date), MONTH(o.order_date) ORDER BY YEAR(o.order_date), MONTH(o.order_date)", nativeQuery = true)
    List<Object[]> findMonthlyRevenue(@Param("since") LocalDateTime since);

    // Weekly revenue (last 8 weeks)
    @Query(value = "SELECT YEAR(o.order_date), WEEK(o.order_date), COALESCE(SUM(o.total_amount), 0), COUNT(o.id) FROM orders o WHERE o.status = 'DELIVERED' AND o.order_date >= :since GROUP BY YEAR(o.order_date), WEEK(o.order_date) ORDER BY YEAR(o.order_date), WEEK(o.order_date)", nativeQuery = true)
    List<Object[]> findWeeklyRevenue(@Param("since") LocalDateTime since);

    // Top customers by total spending
    @Query(value = "SELECT u.user_id, COALESCE(ud.full_name, u.username), COUNT(o.id), COALESCE(SUM(o.total_amount), 0) FROM orders o JOIN users u ON o.user_id = u.user_id LEFT JOIN user_details ud ON u.user_id = ud.user_id WHERE o.status = 'DELIVERED' GROUP BY u.user_id, ud.full_name, u.username ORDER BY SUM(o.total_amount) DESC", nativeQuery = true)
    List<Object[]> findTopCustomers();

    // Order count by status (for pie chart)
    @Query(value = "SELECT o.status, COUNT(o.id) FROM orders o GROUP BY o.status", nativeQuery = true)
    List<Object[]> countOrdersByStatus();

    // Recent orders (last 7 days revenue vs previous 7 days)
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'DELIVERED' AND o.orderDate >= :since")
    java.math.BigDecimal sumRevenueSince(@Param("since") LocalDateTime since);

    // ========== User orders (customer-facing) ==========

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product LEFT JOIN FETCH o.user u LEFT JOIN FETCH u.userDetail WHERE o.user.userId = :userId ORDER BY o.orderDate DESC")
    List<Order> findByUserWithItems(@Param("userId") String userId);

    @Query(value = "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product LEFT JOIN FETCH o.user u LEFT JOIN FETCH u.userDetail WHERE o.user.userId = :userId",
           countQuery = "SELECT COUNT(o) FROM Order o WHERE o.user.userId = :userId")
    Page<Order> findByUserWithItems(@Param("userId") String userId, Pageable pageable);

    @Query(value = "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product LEFT JOIN FETCH o.user u LEFT JOIN FETCH u.userDetail WHERE o.user.userId = :userId AND o.status = :status",
           countQuery = "SELECT COUNT(o) FROM Order o WHERE o.user.userId = :userId AND o.status = :status")
    Page<Order> findByUserAndStatusWithItems(@Param("userId") String userId, @Param("status") OrderStatus status, Pageable pageable);

    // ========== Admin orders ==========

    @Query(value = "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product LEFT JOIN FETCH o.user u LEFT JOIN FETCH u.userDetail",
           countQuery = "SELECT COUNT(o) FROM Order o")
    Page<Order> findAllWithItems(Pageable pageable);

    @Query(value = "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product LEFT JOIN FETCH o.user u LEFT JOIN FETCH u.userDetail WHERE o.status = :status",
           countQuery = "SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Page<Order> findByStatusWithItems(@Param("status") OrderStatus status, Pageable pageable);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product LEFT JOIN FETCH o.user u LEFT JOIN FETCH u.userDetail WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);
}
