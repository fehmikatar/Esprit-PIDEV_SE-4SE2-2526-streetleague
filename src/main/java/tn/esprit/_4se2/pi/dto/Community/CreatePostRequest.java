package tn.esprit._4se2.pi.dto.Community;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePostRequest {

    @NotBlank(message = "Post content must not be blank")
    @Size(max = 5000, message = "Post content must not exceed 5000 characters")
    private String content;
}
