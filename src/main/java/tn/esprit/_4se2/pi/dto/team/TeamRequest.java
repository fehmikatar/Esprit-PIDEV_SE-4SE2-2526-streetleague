package tn.esprit._4se2.pi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRequest {

    @NotBlank(message = "Team name is required")
    private String name;

    @NotBlank(message = "Sport category is required")
    private String sport;

    private String level;

    private String description;

    private String city;

    private String logo;
}
