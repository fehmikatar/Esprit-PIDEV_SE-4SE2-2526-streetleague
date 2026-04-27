package tn.esprit._4se2.pi.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit._4se2.pi.entities.ChatMessage;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByRoomIdOrderBySentAtDesc(String roomId, Pageable pageable);

    List<ChatMessage> findByRoomIdAndIdLessThanOrderBySentAtDesc(String roomId, Long lastMessageId, Pageable pageable);

    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.roomId = :roomId AND m.isRead = false AND m.sender.id <> :userId")
    long countUnreadMessages(@Param("roomId") String roomId, @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true, m.readAt = CURRENT_TIMESTAMP WHERE m.roomId = :roomId AND m.sender.id <> :userId AND m.isRead = false")
    void markMessagesAsRead(@Param("roomId") String roomId, @Param("userId") Long userId);

    List<ChatMessage> findBySender_IdAndReceiver_IdOrderBySentAtDesc(Long senderId, Long receiverId, Pageable pageable);

    @Query("SELECT DISTINCT m.roomId FROM ChatMessage m WHERE m.sender.id = :userId OR m.receiver.id = :userId")
    List<String> findDistinctRoomIdsByUserId(@Param("userId") Long userId);
}