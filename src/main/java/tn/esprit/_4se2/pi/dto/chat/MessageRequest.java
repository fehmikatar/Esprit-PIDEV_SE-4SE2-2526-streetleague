package tn.esprit._4se2.pi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageRequest {
    @NotBlank(message = "Le contenu du message est obligatoire")
    private String content;

    @NotNull(message = "L'expéditeur est obligatoire")
    private Long senderId;

    @NotNull(message = "L'équipe destinataire est obligatoire")
    private Long teamId;
}
