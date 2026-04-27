package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< Updated upstream
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}
=======
import tn.esprit._4se2.pi.entities.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

    void deleteByTeam_Id(Long teamId);
}
>>>>>>> Stashed changes
