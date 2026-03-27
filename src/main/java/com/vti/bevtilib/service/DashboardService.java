package com.vti.bevtilib.service;

import com.vti.bevtilib.dto.DashboardStatsDTO;
import com.vti.bevtilib.model.OrderStatus;
import com.vti.bevtilib.repository.OrderRepository;
import com.vti.bevtilib.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    private static final Map<String, String> STATUS_LABELS = Map.of(
            "PENDING", "Cho xu ly",
            "CONFIRMED", "Da xac nhan",
            "SHIPPING", "Dang giao",
            "DELIVERED", "Da giao",
            "CANCELLED", "Da huy"
    );

    @Transactional(readOnly = true)
    public DashboardStatsDTO getAdvancedStats() {
        // === User stats ===
        long totalAdmins = userRepository.countByRole("ADMIN");
        long totalStaff = userRepository.countByRole("STAFF");
        long totalCustomers = userRepository.countByRole("CUSTOMER");
        long lockedAccounts = userRepository.countByStatus(false);

        // === Order stats ===
        long totalOrders = orderRepository.countAllOrders();
        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        long confirmedOrders = orderRepository.countByStatus(OrderStatus.CONFIRMED);
        long shippingOrders = orderRepository.countByStatus(OrderStatus.SHIPPING);
        long deliveredOrders = orderRepository.countByStatus(OrderStatus.DELIVERED);
        long cancelledOrders = orderRepository.countByStatus(OrderStatus.CANCELLED);

        double cancelRate = totalOrders > 0 ? (double) cancelledOrders / totalOrders * 100 : 0;

        // === Revenue stats ===
        BigDecimal totalRevenue = orderRepository.sumTotalRevenue();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfThisWeek = now.minusDays(7).with(LocalTime.MIN);
        LocalDateTime startOfPrevWeek = startOfThisWeek.minusDays(7);

        BigDecimal revenueThisWeek = orderRepository.sumRevenueSince(startOfThisWeek);
        BigDecimal revenuePrevWeekTotal = orderRepository.sumRevenueByDateRange(startOfPrevWeek, startOfThisWeek);

        double growthPercent = 0;
        if (revenuePrevWeekTotal.compareTo(BigDecimal.ZERO) > 0) {
            growthPercent = revenueThisWeek.subtract(revenuePrevWeekTotal)
                    .divide(revenuePrevWeekTotal, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        // === Daily revenue (last 30 days) ===
        LocalDateTime thirtyDaysAgo = now.minusDays(30).with(LocalTime.MIN);
        List<Object[]> dailyData = orderRepository.findDailyRevenue(thirtyDaysAgo, now);
        List<DashboardStatsDTO.RevenueChartData> dailyRevenue = dailyData.stream()
                .map(row -> DashboardStatsDTO.RevenueChartData.builder()
                        .label(row[0].toString())
                        .revenue(toBigDecimal(row[1]))
                        .orderCount(((Number) row[2]).longValue())
                        .build())
                .collect(Collectors.toList());

        // === Weekly revenue (last 8 weeks) ===
        LocalDateTime eightWeeksAgo = now.minusWeeks(8).with(LocalTime.MIN);
        List<Object[]> weeklyData = orderRepository.findWeeklyRevenue(eightWeeksAgo);
        List<DashboardStatsDTO.RevenueChartData> weeklyRevenue = weeklyData.stream()
                .map(row -> DashboardStatsDTO.RevenueChartData.builder()
                        .label("T" + row[1])
                        .revenue(toBigDecimal(row[2]))
                        .orderCount(((Number) row[3]).longValue())
                        .build())
                .collect(Collectors.toList());

        // === Monthly revenue (last 12 months) ===
        LocalDateTime twelveMonthsAgo = now.minusMonths(12).withDayOfMonth(1).with(LocalTime.MIN);
        List<Object[]> monthlyData = orderRepository.findMonthlyRevenue(twelveMonthsAgo);
        List<DashboardStatsDTO.RevenueChartData> monthlyRevenue = monthlyData.stream()
                .map(row -> DashboardStatsDTO.RevenueChartData.builder()
                        .label(row[1] + "/" + row[0])
                        .revenue(toBigDecimal(row[2]))
                        .orderCount(((Number) row[3]).longValue())
                        .build())
                .collect(Collectors.toList());

        // === Top products ===
        LocalDateTime sixMonthsAgo = now.minusMonths(6).with(LocalTime.MIN);
        List<Object[]> topProductData = orderRepository.findTopProductsByRevenue(sixMonthsAgo, now);
        List<DashboardStatsDTO.TopProduct> topProducts = topProductData.stream()
                .limit(10)
                .map(row -> DashboardStatsDTO.TopProduct.builder()
                        .productName((String) row[0])
                        .quantitySold(((Number) row[1]).longValue())
                        .revenue(toBigDecimal(row[2]))
                        .build())
                .collect(Collectors.toList());

        // === Top customers ===
        List<Object[]> topCustomerData = orderRepository.findTopCustomers();
        List<DashboardStatsDTO.TopCustomer> topCustomers = topCustomerData.stream()
                .limit(10)
                .map(row -> DashboardStatsDTO.TopCustomer.builder()
                        .customerId(row[0].toString())
                        .customerName(row[1] != null ? row[1].toString() : "-")
                        .orderCount(((Number) row[2]).longValue())
                        .totalSpent(toBigDecimal(row[3]))
                        .build())
                .collect(Collectors.toList());

        // === Order status distribution ===
        List<Object[]> statusData = orderRepository.countOrdersByStatus();
        List<DashboardStatsDTO.OrderStatusCount> orderStatusDistribution = statusData.stream()
                .map(row -> {
                    String statusName = row[0].toString();
                    return DashboardStatsDTO.OrderStatusCount.builder()
                            .status(statusName)
                            .label(STATUS_LABELS.getOrDefault(statusName, statusName))
                            .count(((Number) row[1]).longValue())
                            .build();
                })
                .collect(Collectors.toList());

        return DashboardStatsDTO.builder()
                .totalUsers(totalAdmins + totalStaff + totalCustomers)
                .totalCustomers(totalCustomers)
                .totalStaff(totalStaff)
                .totalAdmins(totalAdmins)
                .lockedAccounts(lockedAccounts)
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .confirmedOrders(confirmedOrders)
                .shippingOrders(shippingOrders)
                .deliveredOrders(deliveredOrders)
                .cancelledOrders(cancelledOrders)
                .cancelRate(Math.round(cancelRate * 100.0) / 100.0)
                .totalRevenue(totalRevenue)
                .revenueThisWeek(revenueThisWeek)
                .revenuePrevWeek(revenuePrevWeekTotal)
                .revenueGrowthPercent(Math.round(growthPercent * 100.0) / 100.0)
                .dailyRevenue(dailyRevenue)
                .weeklyRevenue(weeklyRevenue)
                .monthlyRevenue(monthlyRevenue)
                .topProducts(topProducts)
                .topCustomers(topCustomers)
                .orderStatusDistribution(orderStatusDistribution)
                .build();
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        return new BigDecimal(value.toString());
    }
}
