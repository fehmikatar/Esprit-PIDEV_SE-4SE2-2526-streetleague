package tn.esprit._4se2.pi.dto;

import lombok.*;
import tn.esprit._4se2.pi.Enum.PostStatus;
import tn.esprit._4se2.pi.Enum.PostType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPostResponse {

    private Long id;

    // Author info
    private Long authorId;
    private String authorFirstName;
    private String authorLastName;

    private String content;
    private PostType postType;
    private PostStatus status;

    // Team info (nullable — only present for TEAM posts)
    private Long teamId;
    private String teamName;

    // Community info (nullable for legacy/global posts)
    private Long communityId;
    private String communityName;

    private String imageUrl;
    private int likesCount;
    private int commentsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Context for the currently authenticated user
    private boolean isLikedByCurrentUser;
}
