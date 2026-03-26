package tn.esprit._4se2.pi.dto.Feedback;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.*;
import tn.esprit._4se2.pi.dto.SportSpace.SportSpaceRequest;
import tn.esprit._4se2.pi.dto.User.UserRequest;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackRequest {

    @NotNull
    Long userId;

    @NotNull
    Long sportSpaceId;

    @NotNull
    @Min(1) @Max(5)
    Integer rating;

    @NotBlank
    @Size(min = 10, max = 1000)
    String comment;
}