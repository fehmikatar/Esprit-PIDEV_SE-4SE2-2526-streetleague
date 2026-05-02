package tn.esprit._4se2.pi.dto.Community;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponse {

    private Long id;
    private String content;
    private PostResponse.AuthorInfo author;
    private Long postId;
    private LocalDateTime createdAt;
}
