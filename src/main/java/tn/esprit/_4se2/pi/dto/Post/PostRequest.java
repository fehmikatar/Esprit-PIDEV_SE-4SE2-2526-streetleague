package tn.esprit._4se2.pi.dto.Post;

import lombok.Data;

@Data
public class PostRequest {
    private String title;
    private String content;

    // relations (ex: category, user…)
    private Long categoryId;
}
