package tn.esprit._4se2.pi.services.Post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.entities.Category;
import tn.esprit._4se2.pi.entities.Post;
import tn.esprit._4se2.pi.repositories.CategoryRepository;
import tn.esprit._4se2.pi.repositories.PostRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Post addPost(Post post) {
        // Set creation timestamp
        post.setCreatedAt(LocalDateTime.now());

        // Handle category: fetch managed entity if ID provided
        if (post.getCategory() != null && post.getCategory().getId() != null) {
            Category category = categoryRepository.findById(post.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + post.getCategory().getId()));
            post.setCategory(category);
        } else {
            // No category provided → set to null (post without category)
            post.setCategory(null);
        }

        return postRepository.save(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }

    @Override
    @Transactional
    public Post updatePost(Post post) {
        // Fetch existing post to preserve fields not sent in request
        Post existingPost = getPostById(post.getId());

        // Update editable fields
        existingPost.setTitle(post.getTitle());
        existingPost.setContent(post.getContent());

        // Update category
        if (post.getCategory() != null && post.getCategory().getId() != null) {
            Category category = categoryRepository.findById(post.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + post.getCategory().getId()));
            existingPost.setCategory(category);
        } else if (post.getCategory() == null) {
            // Explicit null removes the category association
            existingPost.setCategory(null);
        }
        // If category is provided without ID, we ignore it (keep existing)

        // The managed entity is automatically updated at transaction commit
        return existingPost;
    }

    @Override
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}