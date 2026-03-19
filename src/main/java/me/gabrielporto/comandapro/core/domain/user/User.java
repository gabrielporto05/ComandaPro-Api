package me.gabrielporto.comandapro.core.domain.user;

import java.time.OffsetDateTime;
import java.util.UUID;
/**
 * Domínio puro (sem anotações de infraestrutura).
 */
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

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        touch();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        touch();
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
        touch();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        touch();
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
        touch();
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
        touch();
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    private void touch() {
        this.updatedAt = OffsetDateTime.now();
    }

}
