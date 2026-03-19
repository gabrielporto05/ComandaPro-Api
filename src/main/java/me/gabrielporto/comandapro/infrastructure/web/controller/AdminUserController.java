package me.gabrielporto.comandapro.infrastructure.web.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.gabrielporto.comandapro.application.user.UserService;
import me.gabrielporto.comandapro.core.domain.store.Store;
import me.gabrielporto.comandapro.core.domain.user.User;
import me.gabrielporto.comandapro.infrastructure.web.dto.response.AdminStoreSummaryResponse;
import me.gabrielporto.comandapro.infrastructure.web.dto.response.AdminUserResponse;
import me.gabrielporto.comandapro.infrastructure.web.dto.response.ApiResponse;

@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> listAllUsers() {
        List<User> users = userService.listAll();
        List<UUID> userIds = users.stream().map(User::getId).toList();

        Map<UUID, Store> storesByUser = userService.findStoresByUserIds(userIds);

        List<AdminUserResponse> response = users.stream()
                .map(user -> new AdminUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getTel(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getCreatedAt(),
                toStoreSummary(storesByUser.get(user.getId()))
        ))
                .toList();

        return ResponseEntity.ok(new ApiResponse(
                "Usuários listados com sucesso",
                response
        ));
    }

    private AdminStoreSummaryResponse toStoreSummary(Store store) {
        if (store == null) {
            return null;
        }
        return new AdminStoreSummaryResponse(
                store.getId(),
                store.getName(),
                store.getSlug(),
                store.getEmail(),
                store.getTel(),
                store.getStatus().name()
        );
    }
}
