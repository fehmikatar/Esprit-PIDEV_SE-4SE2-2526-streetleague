package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.esprit._4se2.pi.entities.Team;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Team findByName(String name);

    @Query(value = """
        SELECT t.*
        FROM teams t
        LEFT JOIN `user` u ON u.id = t.created_by_id
        WHERE t.created_by_id IS NULL OR u.id IS NOT NULL
        """, nativeQuery = true)
    List<Team> findAllSafe();

    @Query(value = """
        SELECT t.*
        FROM teams t
        LEFT JOIN `user` u ON u.id = t.created_by_id
        WHERE t.id = :id
          AND (t.created_by_id IS NULL OR u.id IS NOT NULL)
        """, nativeQuery = true)
    Optional<Team> findSafeById(Long id);
}
