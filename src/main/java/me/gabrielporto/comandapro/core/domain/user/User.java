package me.gabrielporto.comandapro.core.domain.user;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

    private UUID id = UUID.randomUUID();
    private String name;
    private String email;
    private String tel;
    private String password;
    private Role role = Role.OWNER;
    private UserStatus status = UserStatus.ACTIVE;
    private OffsetDateTime createdAt = OffsetDateTime.now();
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public User() {
    }

    public User(String name, String email, String tel, String password) {
        this.name = name;
        this.email = email;
        this.tel = tel;
        this.password = password;
    }

    private void touch() {
        this.updatedAt = OffsetDateTime.now();
    }
}
