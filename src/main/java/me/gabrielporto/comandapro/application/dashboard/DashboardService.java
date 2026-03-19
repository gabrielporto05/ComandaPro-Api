package me.gabrielporto.comandapro.application.dashboard;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.gabrielporto.comandapro.application.dashboard.DashboardDtos.CategoriesChartResponse;
import me.gabrielporto.comandapro.application.dashboard.DashboardDtos.CategoriesTotal;
import me.gabrielporto.comandapro.application.dashboard.DashboardDtos.CategorySlice;
import me.gabrielporto.comandapro.application.dashboard.DashboardDtos.DailyStatsResponse;
import me.gabrielporto.comandapro.application.dashboard.DashboardDtos.FullDashboardResponse;
import me.gabrielporto.comandapro.application.dashboard.DashboardDtos.MetricComparison;
import me.gabrielporto.comandapro.application.dashboard.DashboardDtos.OrderChartPoint;
import me.gabrielporto.comandapro.application.dashboard.DashboardDtos.OrdersChartResponse;
import me.gabrielporto.comandapro.application.dashboard.DashboardDtos.RecentOrderItem;
import me.gabrielporto.comandapro.application.dashboard.DashboardDtos.RecentOrdersResponse;
import me.gabrielporto.comandapro.application.dashboard.DashboardDtos.TopProductItem;
import me.gabrielporto.comandapro.application.dashboard.DashboardDtos.TopProductsResponse;
import me.gabrielporto.comandapro.core.domain.order.Order;
import me.gabrielporto.comandapro.core.domain.order.OrderItem;
import me.gabrielporto.comandapro.core.domain.order.OrderStatus;
import me.gabrielporto.comandapro.core.domain.store.Product;
import me.gabrielporto.comandapro.infrastructure.persistence.repository.OrderJpaRepository;
import me.gabrielporto.comandapro.infrastructure.persistence.repository.StoreJpaRepository;
import me.gabrielporto.comandapro.shared.exception.BusinessException;

@Service
public class DashboardService {

    private static final String[] CATEGORY_COLORS = {
        "#10b981", "#3b82f6", "#f59e0b", "#ef4444", "#8b5cf6", "#14b8a6", "#6366f1", "#ec4899"
    };

    private final OrderJpaRepository orderRepository;
    private final StoreJpaRepository storeRepository;

    public DashboardService(OrderJpaRepository orderRepository, StoreJpaRepository storeRepository) {
        this.orderRepository = orderRepository;
        this.storeRepository = storeRepository;
    }

    @Transactional(readOnly = true)
    public DailyStatsResponse getDailyStats(UUID storeId, UUID userId) {
        validateStoreOwner(storeId, userId);

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        List<Order> todayOrders = orderRepository.findByStoreIdAndCreatedAtDate(storeId, today);
        List<Order> yesterdayOrders = orderRepository.findByStoreIdAndCreatedAtDate(storeId, yesterday);

        MetricComparison revenue = buildMetric(sumRevenue(todayOrders), sumRevenue(yesterdayOrders));
        MetricComparison orders = buildMetric(countOrders(todayOrders), countOrders(yesterdayOrders));
        MetricComparison customers = buildMetric(countUniqueCustomers(todayOrders), countUniqueCustomers(yesterdayOrders));
        MetricComparison averageTicket = buildMetric(averageTicket(todayOrders), averageTicket(yesterdayOrders));

        return new DailyStatsResponse(revenue, orders, customers, averageTicket);
    }

    @Transactional(readOnly = true)
    public OrdersChartResponse getOrdersChart(UUID storeId, UUID userId, int days) {
        validateStoreOwner(storeId, userId);

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        List<Order> orders = orderRepository.findByStoreIdAndCreatedAtBetween(storeId, startDateTime, endDateTime);

        Map<LocalDate, DayAggregated> byDate = orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .collect(Collectors.groupingBy(
                        o -> o.getCreatedAt().toLocalDate(),
                        Collectors.collectingAndThen(Collectors.toList(), this::aggregateDay)
                ));

        List<OrderChartPoint> points = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DayAggregated agg = byDate.getOrDefault(date, new DayAggregated(0L, 0.0));
            points.add(new OrderChartPoint(
                    dayLabel(date),
                    date.getDayOfWeek().getValue(),
                    date,
                    agg.orders(),
                    agg.revenue()
            ));
        }

        return new OrdersChartResponse(points);
    }

    @Transactional(readOnly = true)
    public CategoriesChartResponse getCategoriesChart(UUID storeId, UUID userId, int days) {
        validateStoreOwner(storeId, userId);

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        List<Order> orders = orderRepository.findByStoreIdAndCreatedAtBetween(
                storeId,
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        );

        Map<String, CategoryAggregated> aggregated = new LinkedHashMap<>();
        orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .flatMap(o -> o.getItems().stream())
                .forEach(item -> {
                    String category = resolveCategory(item.getProduct());
                    CategoryAggregated current = aggregated.getOrDefault(category, new CategoryAggregated(0L, 0.0));
                    aggregated.put(category, current.add(item.getQuantity(), item.getTotalPrice()));
                });

        long totalOrders = aggregated.values().stream().mapToLong(CategoryAggregated::orders).sum();
        double totalRevenue = aggregated.values().stream().mapToDouble(CategoryAggregated::revenue).sum();

        List<CategorySlice> slices = new ArrayList<>();
        int colorIndex = 0;
        for (var entry : aggregated.entrySet()) {
            double percent = totalOrders == 0 ? 0.0 : (entry.getValue().orders() * 100.0) / totalOrders;
            String color = CATEGORY_COLORS[colorIndex % CATEGORY_COLORS.length];
            slices.add(new CategorySlice(entry.getKey(), round1(percent), entry.getValue().orders(),
                    round2(entry.getValue().revenue()), color));
            colorIndex++;
        }

        return new CategoriesChartResponse(
                slices,
                new CategoriesTotal(totalOrders, round2(totalRevenue))
        );
    }

    @Transactional(readOnly = true)
    public TopProductsResponse getTopProducts(UUID storeId, UUID userId, int days, int limit) {
        validateStoreOwner(storeId, userId);

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        LocalDate previousStart = startDate.minusDays(days);
        LocalDate previousEnd = startDate.minusDays(1);

        List<Order> currentOrders = orderRepository.findByStoreIdAndCreatedAtBetween(
                storeId,
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        );

        List<Order> previousOrders = orderRepository.findByStoreIdAndCreatedAtBetween(
                storeId,
                previousStart.atStartOfDay(),
                previousEnd.plusDays(1).atStartOfDay()
        );

        Map<UUID, ProductAggregation> current = aggregateProducts(currentOrders);
        Map<UUID, ProductAggregation> previous = aggregateProducts(previousOrders);

        List<TopProductItem> items = current.values().stream()
                .sorted(Comparator.comparing(ProductAggregation::sales).reversed()
                        .thenComparing(ProductAggregation::revenue, Comparator.reverseOrder()))
                .limit(limit)
                .map(agg -> {
                    ProductAggregation prev = previous.getOrDefault(agg.productId(), ProductAggregation.empty(agg.product()));
                    double trend = computeTrend(agg.sales(), prev.sales());
                    return new TopProductItem(
                            agg.productId(),
                            agg.productName(),
                            agg.sales(),
                            round2(agg.revenue()),
                            round1(trend),
                            agg.image(),
                            0
                    );
                })
                .toList();

        List<TopProductItem> withPosition = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            TopProductItem item = items.get(i);
            withPosition.add(new TopProductItem(
                    item.productId(),
                    item.name(),
                    item.sales(),
                    item.revenue(),
                    item.trend(),
                    item.image(),
                    i + 1
            ));
        }

        return new TopProductsResponse("last" + days + "days", withPosition);
    }

    @Transactional(readOnly = true)
    public RecentOrdersResponse getRecentOrders(UUID storeId, UUID userId, int limit) {
        validateStoreOwner(storeId, userId);
        List<Order> orders = orderRepository.findByStoreIdOrderByCreatedAtDesc(storeId,
                org.springframework.data.domain.PageRequest.of(0, limit));

        LocalDateTime now = LocalDateTime.now();
        List<RecentOrderItem> items = orders.stream()
                .map(order -> {
                    long itemsCount = order.getItems().stream()
                            .mapToLong(i -> i.getQuantity() != null ? i.getQuantity() : 0)
                            .sum();
                    String timeAgo = humanizeTimeAgo(order.getCreatedAt(), now);
                    String statusLabel = toStatusLabel(order.getStatus());
                    return new RecentOrderItem(
                            order.getId(),
                            order.getOrderCode(),
                            order.getCustomerName(),
                            initials(order.getCustomerName()),
                            itemsCount,
                            round2(order.getTotalAmount()),
                            order.getStatus().name(),
                            statusLabel,
                            timeAgo,
                            order.getCreatedAt()
                    );
                })
                .toList();

        return new RecentOrdersResponse(items);
    }

    @Transactional(readOnly = true)
    public FullDashboardResponse getFullDashboard(UUID storeId, UUID userId, int days, int topLimit, int recentLimit) {
        validateStoreOwner(storeId, userId);
        return new FullDashboardResponse(
                getDailyStats(storeId, userId),
                getOrdersChart(storeId, userId, days),
                getCategoriesChart(storeId, userId, days),
                getTopProducts(storeId, userId, days, topLimit),
                getRecentOrders(storeId, userId, recentLimit)
        );
    }

    private Map<UUID, ProductAggregation> aggregateProducts(List<Order> orders) {
        Map<UUID, ProductAggregation> map = new LinkedHashMap<>();
        orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .flatMap(o -> o.getItems().stream())
                .forEach(item -> {
                    Product product = item.getProduct();
                    UUID productId = product != null ? product.getId() : UUID.nameUUIDFromBytes(item.getProductName().getBytes());
                    ProductAggregation current = map.getOrDefault(productId, ProductAggregation.from(item));
                    map.put(productId, current.add(item.getQuantity(), item.getTotalPrice()));
                });
        return map;
    }

    private DayAggregated aggregateDay(List<Order> orders) {
        long totalOrders = orders.size();
        double revenue = orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .mapToDouble(o -> o.getTotalAmount() != null ? o.getTotalAmount() : 0.0)
                .sum();
        return new DayAggregated(totalOrders, revenue);
    }

    private MetricComparison buildMetric(double today, double yesterday) {
        double diff = today - yesterday;
        double change = yesterday == 0 ? (today > 0 ? 100.0 : 0.0) : (diff / yesterday) * 100.0;
        String trend = diff > 0 ? "up" : (diff < 0 ? "down" : "equal");
        return new MetricComparison(round2(today), round2(yesterday), round1(change), trend);
    }

    private double sumRevenue(List<Order> orders) {
        return orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .mapToDouble(o -> o.getTotalAmount() != null ? o.getTotalAmount() : 0.0)
                .sum();
    }

    private long countOrders(List<Order> orders) {
        return orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .count();
    }

    private long countUniqueCustomers(List<Order> orders) {
        return orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .map(Order::getCustomerPhone)
                .filter(phone -> phone != null && !phone.isBlank())
                .distinct()
                .count();
    }

    private double averageTicket(List<Order> orders) {
        long count = countOrders(orders);
        return count == 0 ? 0.0 : sumRevenue(orders) / count;
    }

    private String humanizeTimeAgo(LocalDateTime createdAt, LocalDateTime now) {
        Duration duration = Duration.between(createdAt, now);
        long minutes = duration.toMinutes();
        if (minutes < 60) {
            return minutes <= 1 ? "1 min atrás" : minutes + " min atrás";
        }
        long hours = duration.toHours();
        if (hours < 24) {
            return hours == 1 ? "1 hora atrás" : hours + " horas atrás";
        }
        long days = duration.toDays();
        return days == 1 ? "1 dia atrás" : days + " dias atrás";
    }

    private String toStatusLabel(OrderStatus status) {
        return switch (status) {
            case PENDING ->
                "recebido";
            case PREPARING ->
                "preparando";
            case READY ->
                "saiu";
            case DELIVERED ->
                "entregue";
            case CANCELLED ->
                "cancelado";
        };
    }

    private String initials(String name) {
        if (name == null || name.isBlank()) {
            return "";
        }
        String[] parts = name.trim().split(" ");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    private String resolveCategory(Product product) {
        if (product != null && product.getCategory() != null && !product.getCategory().isBlank()) {
            return product.getCategory();
        }
        return "Outros";
    }

    private String dayLabel(LocalDate date) {
        return switch (date.getDayOfWeek()) {
            case MONDAY ->
                "Seg";
            case TUESDAY ->
                "Ter";
            case WEDNESDAY ->
                "Qua";
            case THURSDAY ->
                "Qui";
            case FRIDAY ->
                "Sex";
            case SATURDAY ->
                "Sáb";
            case SUNDAY ->
                "Dom";
        };
    }

    private double computeTrend(long current, long previous) {
        if (previous == 0) {
            return current > 0 ? 100.0 : 0.0;
        }
        return ((double) (current - previous) / previous) * 100.0;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private void validateStoreOwner(UUID storeId, UUID userId) {
        var store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException("Loja não encontrada"));
        if (!store.getUser().getId().equals(userId)) {
            throw new BusinessException("Você não tem permissão para acessar esta loja");
        }
    }

    private record DayAggregated(long orders, double revenue) {

    }

    private record CategoryAggregated(long orders, double revenue) {

        CategoryAggregated add(Integer quantity, Double totalPrice) {
            long qty = quantity != null ? quantity : 0;
            double total = totalPrice != null ? totalPrice : 0.0;
            return new CategoryAggregated(this.orders + qty, this.revenue + total);
        }
    }

    private record ProductAggregation(UUID productId, String productName, Product product, long sales, double revenue,
            String image) {

        ProductAggregation add(Integer quantity, Double totalPrice) {
            long qty = quantity != null ? quantity : 0;
            double total = totalPrice != null ? totalPrice : 0.0;
            return new ProductAggregation(productId, productName, product, sales + qty, revenue + total, image);
        }

        static ProductAggregation from(OrderItem item) {
            Product product = item.getProduct();
            UUID productId = product != null ? product.getId() : UUID.nameUUIDFromBytes(item.getProductName().getBytes());
            String image = product != null ? product.getPhotoUrl() : null;
            return new ProductAggregation(productId, item.getProductName(), product, 0L, 0.0, image);
        }

        static ProductAggregation empty(Product product) {
            UUID id = product != null ? product.getId() : UUID.randomUUID();
            return new ProductAggregation(id, product != null ? product.getName() : "", product, 0L, 0.0, product != null ? product.getPhotoUrl() : null);
        }
    }
}
