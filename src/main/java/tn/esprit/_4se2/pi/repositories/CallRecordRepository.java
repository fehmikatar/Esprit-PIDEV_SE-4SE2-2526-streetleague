package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.CallRecord;

import java.util.List;
import java.util.Optional;

@Repository
public interface CallRecordRepository extends JpaRepository<CallRecord, Long> {
    List<CallRecord> findByRoomIdOrderByStartedAtDesc(String roomId);
    List<CallRecord> findByTeamIdOrderByStartedAtDesc(Long teamId);
    List<CallRecord> findByCallerIdOrCalleeIdOrderByStartedAtDesc(Long callerId, Long calleeId);
    
    // Pour retrouver l'appel actif lors d'une signalisation
    Optional<CallRecord> findTopByRoomIdAndCallerIdAndCalleeIdOrderByStartedAtDesc(String roomId, Long callerId, Long calleeId);
}
