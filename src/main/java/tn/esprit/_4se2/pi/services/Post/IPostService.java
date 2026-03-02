package tn.esprit._4se2.pi.services.Post;

import tn.esprit._4se2.pi.entities.Post;

import java.util.List;

public interface IPostService {
    Post addPost(Post post);

    List<Post> getAllPosts();

    Post getPostById(Long id);

    Post updatePost(Post post);

    void deletePost(Long id);
}
