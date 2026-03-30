package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit._4se2.pi.entities.PasswordResetToken;
import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    // Supprimer les anciens tokens d'un user avant d'en créer un nouveau
    void deleteAllByUserId(Long userId);
}