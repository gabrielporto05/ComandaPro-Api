package me.gabrielporto.comandapro.adapters.out.persistence.entities;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import me.gabrielporto.comandapro.core.domain.user.Role;
import me.gabrielporto.comandapro.core.domain.user.UserStatus;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @Column(nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String tel;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    // getters/setters
}
