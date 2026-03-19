package me.gabrielporto.comandapro.application.dashboard;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class DashboardDtos {

    public static record MetricComparison(
            double today,
            double yesterday,
            double change,
            String trend
            ) {

    }

    public static record DailyStatsResponse(
            MetricComparison revenue,
            MetricComparison orders,
            MetricComparison customers,
            MetricComparison averageTicket
            ) {

    }

    public static record OrderChartPoint(
            String day,
            int dayOfWeek,
            LocalDate date,
            long orders,
            double revenue
            ) {

    }

    public static record OrdersChartResponse(
            List<OrderChartPoint> daily
            ) {

    }

    public static record CategorySlice(
            String name,
            double value,
            long orders,
            double revenue,
            String color
            ) {

    }

    public static record CategoriesTotal(
            long orders,
            double revenue
            ) {

    }

    public static record CategoriesChartResponse(
            List<CategorySlice> categories,
            CategoriesTotal total
            ) {

    }

    public static record TopProductItem(
            UUID productId,
            String name,
            long sales,
            double revenue,
            double trend,
            String image,
            int position
            ) {

    }

    public static record TopProductsResponse(
            String period,
            List<TopProductItem> products
            ) {

    }

    public static record RecentOrderItem(
            UUID id,
            String orderCode,
            String customerName,
            String customerInitials,
            long itemsCount,
            double total,
            String status,
            String statusLabel,
            String timeAgo,
            LocalDateTime createdAt
            ) {

    }

    public static record RecentOrdersResponse(
            List<RecentOrderItem> orders
            ) {

    }

    public static record FullDashboardResponse(
            DailyStatsResponse dailyStats,
            OrdersChartResponse ordersChart,
            CategoriesChartResponse categoriesChart,
            TopProductsResponse topProducts,
            RecentOrdersResponse recentOrders
            ) {

    }
}
