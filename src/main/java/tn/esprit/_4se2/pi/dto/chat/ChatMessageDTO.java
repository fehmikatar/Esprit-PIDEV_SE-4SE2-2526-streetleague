package tn.esprit._4se2.pi.dto.chat;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private Long id;
    private String content;
    private Long senderId;
    private String senderName;
    private String senderFirstName;
    private String senderLastName;
    private String senderProfileImage;
    private Long receiverId;
    private String receiverName;
    private Long teamId;
    private String teamName;
    private Long communityId;
    private String communityName;
    private String roomId;
    private String roomType;
    private LocalDateTime sentAt;
    private Boolean isRead;
    private Double sentimentScore;
    private String sentimentLabel;
    private Boolean isOwnMessage;
}