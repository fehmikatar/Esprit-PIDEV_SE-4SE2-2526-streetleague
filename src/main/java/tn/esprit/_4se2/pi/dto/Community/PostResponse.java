package tn.esprit._4se2.pi.dto.Community;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostResponse {

    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private AuthorInfo author;
    private Long teamId;
    private LocalDateTime createdAt;
    private int likeCount;
    private int commentCount;
    private boolean likedByCurrentUser;

    @Data
    public static class AuthorInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String profileImageUrl;
    }
}
