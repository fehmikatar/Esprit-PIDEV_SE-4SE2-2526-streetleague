package tn.esprit._4se2.pi.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SportCommunityResponse {
    private Long id;
    private String name;
    private Long sportCategoryId;
    private String sportCategory;
    private LocalDateTime createdAt;
    private int membersCount;
    private int postsCount;
}
