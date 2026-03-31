package tn.esprit._4se2.pi.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageResponse {
    private Long id;
    private String content;
    private LocalDateTime sentAt;

    private Long senderId;
    private Long teamId;
}
