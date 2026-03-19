package me.gabrielporto.comandapro.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.gabrielporto.comandapro.core.domain.store.Store;
import me.gabrielporto.comandapro.core.domain.store.StoreStatus;

@Repository
public interface StoreJpaRepository extends JpaRepository<Store, UUID> {

    @EntityGraph(attributePaths = {"products", "horarios"})
    Optional<Store> findBySlug(String slug);

    @EntityGraph(attributePaths = {"products", "horarios"})
    Optional<Store> findByUserId(UUID userId);

    @EntityGraph(attributePaths = {"products", "horarios"})
    List<Store> findAllByStatus(StoreStatus status);

    @EntityGraph(attributePaths = {"products", "horarios"})
    Optional<Store> findById(UUID id);

    @EntityGraph(attributePaths = {"user"})
    List<Store> findAllByUserIdIn(List<UUID> userIds);

    boolean existsBySlug(String slug);

    boolean existsByUserId(UUID userId);
}
