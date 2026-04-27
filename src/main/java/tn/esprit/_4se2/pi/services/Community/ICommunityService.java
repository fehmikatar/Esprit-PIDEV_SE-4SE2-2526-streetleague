package tn.esprit._4se2.pi.services.Community;

import org.springframework.data.domain.Page;
import tn.esprit._4se2.pi.dto.Communuty.*;

import java.util.List;

public interface ICommunityService {

    List<SportCommunityResponse> getMyCommunities(Long userId);

    List<CommunityPostResponse> getCommunityPosts(Long communityId, Long authenticatedUserId);

    CommunityPostResponse createCommunityPost(Long communityId, CommunityPostRequest request, Long userId);

    List<CommunityMemberResponse> getCommunityMembers(Long communityId, Long authenticatedUserId);

    CommunityPostResponse createPost(CommunityPostRequest request, Long userId);

    Page<CommunityPostResponse> getGlobalPosts(int page, int size, Long authenticatedUserId);

    List<CommunityPostResponse> getTeamPosts(Long teamId, Long authenticatedUserId);

    CommunityCommentResponse addComment(Long postId, CommunityCommentRequest request, Long userId);

    List<CommunityCommentResponse> getComments(Long postId, Long authenticatedUserId);

    void toggleLike(Long postId, Long userId);

    void deletePost(Long postId, Long userId);

    void deleteComment(Long commentId, Long userId);
}