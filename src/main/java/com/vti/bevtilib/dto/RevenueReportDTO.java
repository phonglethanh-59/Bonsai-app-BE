package com.vti.bevtilib.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReportDTO {
    private BigDecimal totalRevenue;
    private long totalOrders;
    private long deliveredOrders;
    private long cancelledOrders;
    private long pendingOrders;
    private List<DailyRevenue> dailyRevenues;
    private List<ProductRevenue> topProducts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyRevenue {
        private String date;
        private BigDecimal revenue;
        private long orderCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductRevenue {
        private String productName;
        private long quantitySold;
        private BigDecimal revenue;
    }
}
