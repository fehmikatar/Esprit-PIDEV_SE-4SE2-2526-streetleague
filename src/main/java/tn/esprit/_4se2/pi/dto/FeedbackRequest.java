package tn.esprit._4se2.pi.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackRequest {

    @NotNull(message = "User ID is required")
    Long userId;

    @NotNull(message = "Sport Space ID is required")
    Long sportSpaceId;

    @NotNull(message = "Rating is required")
    @Min(1) @Max(5)
    Integer rating;

    @NotBlank(message = "Comment is required")
    @Size(min = 10, max = 1000)
    String comment;
}