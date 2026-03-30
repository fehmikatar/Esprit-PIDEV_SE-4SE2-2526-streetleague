package tn.esprit._4se2.pi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import tn.esprit._4se2.pi.Enum.PostType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPostRequest {

    @NotBlank(message = "Content is required")
    @Size(max = 2000, message = "Content cannot exceed 2000 characters")
    private String content;

    @NotNull(message = "Post type is required")
    private PostType postType;

    private Long teamId;

    private String imageUrl;
}
