package me.gabrielporto.comandapro.infrastructure.web.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.gabrielporto.comandapro.application.dashboard.DashboardService;
import me.gabrielporto.comandapro.core.domain.user.User;
import me.gabrielporto.comandapro.infrastructure.web.dto.response.ApiResponse;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stores/{storeId}/daily-stats")
    public ResponseEntity<ApiResponse> getDailyStats(
            @PathVariable UUID storeId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        var data = dashboardService.getDailyStats(storeId, user.getId());
        return ResponseEntity.ok(new ApiResponse(
                "Estatísticas diárias",
                data
        ));
    }

    @GetMapping("/stores/{storeId}/orders-chart")
    public ResponseEntity<ApiResponse> getOrdersChart(
            @PathVariable UUID storeId,
            @RequestParam(defaultValue = "7") int days,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        var data = dashboardService.getOrdersChart(storeId, user.getId(), days);
        return ResponseEntity.ok(new ApiResponse(
                "Gráfico de pedidos por dia",
                data
        ));
    }

    @GetMapping("/stores/{storeId}/categories-chart")
    public ResponseEntity<ApiResponse> getCategoriesChart(
            @PathVariable UUID storeId,
            @RequestParam(defaultValue = "7") int days,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        var data = dashboardService.getCategoriesChart(storeId, user.getId(), days);
        return ResponseEntity.ok(new ApiResponse(
                "Distribuição de vendas por categoria",
                data
        ));
    }

    @GetMapping("/stores/{storeId}/top-products")
    public ResponseEntity<ApiResponse> getTopProducts(
            @PathVariable UUID storeId,
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "5") int limit,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        var data = dashboardService.getTopProducts(storeId, user.getId(), days, limit);
        return ResponseEntity.ok(new ApiResponse(
                "Produtos mais vendidos",
                data
        ));
    }

    @GetMapping("/stores/{storeId}/recent-orders")
    public ResponseEntity<ApiResponse> getRecentOrders(
            @PathVariable UUID storeId,
            @RequestParam(defaultValue = "5") int limit,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        var data = dashboardService.getRecentOrders(storeId, user.getId(), limit);
        return ResponseEntity.ok(new ApiResponse(
                "Pedidos recentes",
                data
        ));
    }

    @GetMapping("/stores/{storeId}/full-dashboard")
    public ResponseEntity<ApiResponse> getFullDashboard(
            @PathVariable UUID storeId,
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "5") int topLimit,
            @RequestParam(defaultValue = "5") int recentLimit,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        var data = dashboardService.getFullDashboard(storeId, user.getId(), days, topLimit, recentLimit);
        return ResponseEntity.ok(new ApiResponse(
                "Dashboard completo",
                data
        ));
    }
}
