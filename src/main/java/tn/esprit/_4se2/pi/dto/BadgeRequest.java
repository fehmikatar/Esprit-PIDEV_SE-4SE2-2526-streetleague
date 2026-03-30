package tn.esprit._4se2.pi.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BadgeRequest {

    @NotBlank(message = "Le nom du badge est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String name;

    @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères")
    private String description;

    @Min(value = 0, message = "Le niveau doit être ≥ 0")
    @Max(value = 10, message = "Le niveau ne peut pas dépasser 10")
    private int level;

    @Min(value = 0, message = "L'XP requis doit être ≥ 0")
    private int requiredXp;

    @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$",
            message = "L'URL de l'icône doit être valide")
    private String iconUrl;
}