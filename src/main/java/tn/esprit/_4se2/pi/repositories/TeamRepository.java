package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< Updated upstream
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.Enum.TeamStatus;
=======
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
>>>>>>> Stashed changes
import tn.esprit._4se2.pi.entities.Team;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

<<<<<<< Updated upstream
    List<Team> findByStatus(TeamStatus status);

    List<Team> findBySportAndStatus(String sport, TeamStatus status);

    List<Team> findByCityAndStatus(String city, TeamStatus status);

    List<Team> findByLevelAndStatus(String level, TeamStatus status);

    List<Team> findBySportAndCityAndStatus(String sport, String city, TeamStatus status);

    List<Team> findBySportAndLevelAndStatus(String sport, String level, TeamStatus status);

    List<Team> findByCityAndLevelAndStatus(String city, String level, TeamStatus status);

    List<Team> findBySportAndCityAndLevelAndStatus(String sport, String city, String level, TeamStatus status);
=======
    @Query(value = """
        SELECT t.*
        FROM teams t
        LEFT JOIN users u ON u.id = t.created_by_id
        WHERE t.created_by_id IS NULL OR u.id IS NOT NULL
        """, nativeQuery = true)
    List<Team> findAllSafe();

    @Query(value = """
        SELECT t.*
        FROM teams t
        LEFT JOIN users u ON u.id = t.created_by_id
        WHERE t.id = :id
          AND (t.created_by_id IS NULL OR u.id IS NOT NULL)
        """, nativeQuery = true)
    Optional<Team> findSafeById(Long id);

        List<Team> findDistinctByCategory_NomContainingIgnoreCaseAndCreatedBy_FirstNameContainingIgnoreCase(
                        String categoryKeyword,
                        String ownerKeyword
        );

        @Modifying(clearAutomatically = true, flushAutomatically = true)
        @Query("""
                UPDATE Team t
                SET t.status = tn.esprit._4se2.pi.Enum.TeamStatus.ARCHIVED
                WHERE t.status = tn.esprit._4se2.pi.Enum.TeamStatus.ACTIVE
                    AND t.createdAt < :cutoff
                    AND NOT EXISTS (
                            SELECT 1 FROM TeamMember tm WHERE tm.team = t
                    )
                """)
        int archiveDormantTeams(@Param("cutoff") LocalDateTime cutoff);
>>>>>>> Stashed changes
}
