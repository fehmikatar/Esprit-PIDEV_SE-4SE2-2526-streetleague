package tn.esprit._4se2.pi.dto.chat;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {
    private String roomId;
    private String roomType;
    private String roomName;
    private Long entityId;
    private LocalDateTime lastActivityAt;
    private Long unreadCount;
    private ChatMessageDTO lastMessage;
}