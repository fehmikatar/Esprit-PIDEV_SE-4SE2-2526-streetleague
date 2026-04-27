package tn.esprit._4se2.pi.dto.Community;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class CommunityPostDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreatePostRequest {
        // title is optional – the frontend auto-derives it from the first line of content
        private String title;

        @NotBlank
        private String content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PostResponse {
        private Long id;
        private String title;
        private String content;
        private LocalDateTime createdAt;
        private Long authorId;
        private String authorFirstName;
        private String authorLastName;
        private String authorName;
        private Long communityId;
        private String communityName;
        private Long categoryId;
        private String categoryName;
        private int likesCount;
        private int commentsCount;
        private boolean likedByCurrentUser;
    }
}