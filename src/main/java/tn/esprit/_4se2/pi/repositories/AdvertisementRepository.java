package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.Advertisement;

import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
    List<Advertisement> findByCompetitionIdAndIsActiveTrue(Long competitionId);
    List<Advertisement> findByMatchIdAndIsActiveTrue(Long matchId);
    List<Advertisement> findByTeamIdAndIsActiveTrue(Long teamId);
}
