package tn.esprit._4se2.pi.services.Community;

import tn.esprit._4se2.pi.dto.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ICommunityService {

    CommunityPostResponse createPost(CommunityPostRequest request, Long userId);

    Page<CommunityPostResponse> getGlobalPosts(int page, int size, Long authenticatedUserId);

    List<CommunityPostResponse> getTeamPosts(Long teamId, Long authenticatedUserId);

    CommunityCommentResponse addComment(Long postId, CommunityCommentRequest request, Long userId);

    List<CommunityCommentResponse> getComments(Long postId);

    void toggleLike(Long postId, Long userId);

    void deletePost(Long postId, Long userId);

    void deleteComment(Long commentId, Long userId);
}
