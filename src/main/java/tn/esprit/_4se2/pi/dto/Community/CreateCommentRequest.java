package tn.esprit._4se2.pi.dto.Community;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCommentRequest {

    @NotBlank(message = "Comment content must not be blank")
    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    private String content;
}
