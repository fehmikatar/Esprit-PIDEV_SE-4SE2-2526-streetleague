package tn.esprit._4se2.pi.dto;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

public class LikeRequest {
    @NotNull(message = "Le postId est obligatoire")
    private Long postId;

    @NotNull(message = "L'userId est obligatoire")
    private Long userId;
}
