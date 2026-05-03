package tn.esprit._4se2.pi.dto.Communuty;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class CommentRequest {
    @NotBlank(message = "Le contenu du commentaire ne peut pas être vide")
    private String content;

    @NotNull(message = "Le postId est obligatoire")
    private Long postId;

    @NotNull(message = "L'userId est obligatoire")
    private Long userId;
}
