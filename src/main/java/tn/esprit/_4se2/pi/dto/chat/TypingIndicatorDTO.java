package tn.esprit._4se2.pi.dto.chat;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypingIndicatorDTO {
    private String roomId;
    private String roomType;
    private String username;
    private boolean isTyping;
}