package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit._4se2.pi.dto.Community.CommunityDTO;
import tn.esprit._4se2.pi.entities.Community;

import java.util.List;
import java.util.Optional;

public interface CommunityRepository extends JpaRepository<Community, Long> {
    Optional<Community> findByCategory_Id(Long categoryId);
    List<Community> findByCategory_IdIn(List<Long> categoryIds);

    @Query("""
        SELECT new tn.esprit._4se2.pi.dto.Community.CommunityDTO(
            c.id,
            c.name,
            c.description,
            cat.id,
            cat.nom,
            COUNT(cm.id),
            c.createdAt
        )
        FROM Community c
        JOIN c.category cat
        LEFT JOIN c.members cm
        LEFT JOIN cm.user u
        WHERE (:categoryKeyword IS NULL OR :categoryKeyword = ''
               OR LOWER(cat.nom) LIKE LOWER(CONCAT('%', :categoryKeyword, '%')))
        GROUP BY c.id, c.name, c.description, cat.id, cat.nom, c.createdAt
        HAVING COUNT(cm.id) >= :minMembers
        ORDER BY COUNT(cm.id) DESC, c.createdAt DESC
        """)
    List<CommunityDTO> findCommunityInsightsByCategory(@Param("categoryKeyword") String categoryKeyword,
                                                       @Param("minMembers") long minMembers);
}