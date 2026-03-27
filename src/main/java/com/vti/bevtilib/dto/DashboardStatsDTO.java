package com.vti.bevtilib.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {

    // User stats
    private long totalUsers;
    private long totalCustomers;
    private long totalStaff;
    private long totalAdmins;
    private long lockedAccounts;

    // Order stats
    private long totalOrders;
    private long pendingOrders;
    private long confirmedOrders;
    private long shippingOrders;
    private long deliveredOrders;
    private long cancelledOrders;

    // Revenue stats
    private BigDecimal totalRevenue;
    private BigDecimal revenueThisWeek;
    private BigDecimal revenuePrevWeek;
    private double revenueGrowthPercent;

    // Chart data
    private List<RevenueChartData> dailyRevenue;
    private List<RevenueChartData> weeklyRevenue;
    private List<RevenueChartData> monthlyRevenue;

    // Rankings
    private List<TopProduct> topProducts;
    private List<TopCustomer> topCustomers;

    // Order status distribution
    private List<OrderStatusCount> orderStatusDistribution;

    // Cancel rate
    private double cancelRate;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueChartData {
        private String label;
        private BigDecimal revenue;
        private long orderCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProduct {
        private String productName;
        private long quantitySold;
        private BigDecimal revenue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopCustomer {
        private String customerId;
        private String customerName;
        private long orderCount;
        private BigDecimal totalSpent;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderStatusCount {
        private String status;
        private String label;
        private long count;
    }
}
