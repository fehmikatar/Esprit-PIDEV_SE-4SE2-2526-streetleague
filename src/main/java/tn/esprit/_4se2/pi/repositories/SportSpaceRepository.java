package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.SportSpace;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SportSpaceRepository extends JpaRepository<SportSpace, Long> {
    List<SportSpace> findByFieldOwnerId(Long fieldOwnerId);
    List<SportSpace> findBySportType(String sportType);
    List<SportSpace> findByIsAvailableTrue();
    List<SportSpace> findByLocationContainingIgnoreCase(String location);
    // ── TÂCHE 2 : Requête JPQL avec JOIN ──────────────────────────────────────
    /**
     * Récupère les statistiques des terrains disponibles en joinant trois tables :
     *   - sport_spaces  (alias s)
     *   - feedbacks     (alias f, relation via f.sportSpaceId = s.id)
     *   - bookings      (alias b, relation via b.sportSpaceId = s.id)
     *
     * Ce que fait cette requête :
     *  - LEFT JOIN Feedback : on garde les terrains sans avis (sinon ils disparaissent
     *    avec un INNER JOIN), et on filtre uniquement les feedbacks "APPROVED".
     *  - LEFT JOIN Booking  : idem, et on exclut les réservations CANCELLED du comptage.
     *  - COALESCE(AVG(...), 0.0) : si aucun feedback, AVG retourne NULL → on le remplace
     *    par 0.0 pour éviter un NullPointerException côté service.
     *  - GROUP BY s        : obligatoire car on utilise AVG() et COUNT() (fonctions d'agrégat).
     *  - ORDER BY AVG DESC : les meilleurs terrains remontent en premier.
     *
     * Retourne des Object[] : chaque tableau contient [SportSpace, Double, Long, Long].
     */
    @Query("""
    SELECT s,
           COALESCE(AVG(CASE WHEN f.status = 'APPROVED' THEN f.rating END), 0.0),
           COUNT(DISTINCT CASE WHEN f.status = 'APPROVED' THEN f.id END),
           COUNT(DISTINCT CASE WHEN b.status <> 'CANCELLED' THEN b.id END)
    FROM SportSpace s
    LEFT JOIN Feedback f ON f.sportSpaceId = s.id
    LEFT JOIN Booking b ON b.sportSpaceId = s.id
    WHERE s.isAvailable = true
    GROUP BY s
    ORDER BY COALESCE(AVG(CASE WHEN f.status = 'APPROVED' THEN f.rating END), 0.0) DESC
    """)
    List<Object[]> findAvailableSpacesWithStats();

    // ── TÂCHE 3 : Derived Query (keywords, plusieurs colonnes) ─────────────────
    /**
     * Derived query (Spring Data Keywords) — Spring génère la requête SQL
     * automatiquement à partir du nom de la méthode :
     *
     *   findBy SportType And IsAvailableTrue And HourlyRateLessThanEqual
     *    ↓               ↓                   ↓
     *   WHERE sport_type = ?  AND is_available = 1  AND hourly_rate <= ?
     *
     * Ce qui est notable :
     *  - 3 colonnes impliquées : sport_type, is_available, hourly_rate.
     *  - Aucun SQL ni JPQL écrits manuellement.
     *  - "IsAvailableTrue" est un mot-clé Spring Data : pas besoin de paramètre booléen.
     *  - "LessThanEqual" génère l'opérateur SQL <=.
     *
     * Utilisation : trouver les terrains de foot disponibles à moins de 50 DT/h.
     */
    List<SportSpace> findBySportTypeAndIsAvailableTrueAndHourlyRateLessThanEqual(
            String sportType,
            BigDecimal maxHourlyRate
    );


}
