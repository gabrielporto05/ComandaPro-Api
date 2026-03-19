package me.gabrielporto.comandapro.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.gabrielporto.comandapro.core.domain.subscription.Subscription;

@Repository
public interface SubscriptionJpaRepository extends JpaRepository<Subscription, UUID> {
}
