package tn.esprit._4se2.pi.mappers;
import tn.esprit._4se2.pi.dto.PostRequest;
import tn.esprit._4se2.pi.dto.PostResponse;
import tn.esprit._4se2.pi.entities.Post;


public class PostMapper {
    public static Post toEntity(PostRequest dto) {
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        return post;
    }

    public static PostResponse toDto(Post post) {
        PostResponse dto = new PostResponse();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        return dto;
    }
}
