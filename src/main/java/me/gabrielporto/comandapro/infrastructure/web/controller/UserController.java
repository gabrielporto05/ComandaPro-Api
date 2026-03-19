package me.gabrielporto.comandapro.infrastructure.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import me.gabrielporto.comandapro.application.user.UserService;
import me.gabrielporto.comandapro.core.domain.user.User;
import me.gabrielporto.comandapro.infrastructure.web.dto.request.UpdateUserRequest;
import me.gabrielporto.comandapro.infrastructure.web.dto.response.ApiResponse;
import me.gabrielporto.comandapro.infrastructure.web.dto.response.UserResponse;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse> updateMe(
            @Valid @RequestBody UpdateUserRequest request,
            Authentication authentication) {

        User authUser = (User) authentication.getPrincipal();

        User user = userService.updateProfile(
                authUser.getId(),
                request.name(),
                request.email(),
                request.currentPassword(),
                request.newPassword()
        );

        UserResponse response = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getTel(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getCreatedAt()
        );

        return ResponseEntity.ok(new ApiResponse(
                "Dados do usuário atualizados com sucesso",
                response
        ));
    }
}
