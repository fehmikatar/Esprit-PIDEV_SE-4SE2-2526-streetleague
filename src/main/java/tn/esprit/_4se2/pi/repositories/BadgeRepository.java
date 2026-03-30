package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit._4se2.pi.entities.Badge;

import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
    // Les méthodes CRUD de base sont déjà fournies par JpaRepository
    Optional<Badge> findByName(String name);
}