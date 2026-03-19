package me.gabrielporto.comandapro.core.ports.in;

import me.gabrielporto.comandapro.core.domain.user.User;

public interface CreateOwnerUseCase {

    User create(User user);
}
