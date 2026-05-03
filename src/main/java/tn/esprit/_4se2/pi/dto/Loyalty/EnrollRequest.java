package tn.esprit._4se2.pi.dto.Loyalty;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Long programId;
}