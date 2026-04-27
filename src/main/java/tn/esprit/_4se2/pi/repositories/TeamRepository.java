package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit._4se2.pi.Enum.TeamStatus;
import tn.esprit._4se2.pi.entities.Team;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {


    List<Team> findByStatus(TeamStatus status);

    List<Team> findBySportAndStatus(String sport, TeamStatus status);

    List<Team> findByCityAndStatus(String city, TeamStatus status);

    List<Team> findByLevelAndStatus(String level, TeamStatus status);

    List<Team> findBySportAndCityAndStatus(String sport, String city, TeamStatus status);

    List<Team> findBySportAndLevelAndStatus(String sport, String level, TeamStatus status);

    List<Team> findByCityAndLevelAndStatus(String city, String level, TeamStatus status);

    List<Team> findBySportAndCityAndLevelAndStatus(String sport, String city, String level, TeamStatus status);
}
