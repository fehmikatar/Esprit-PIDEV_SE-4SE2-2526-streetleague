package tn.esprit._4se2.pi.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.Enum.PostStatus;
import tn.esprit._4se2.pi.Enum.PostType;
import tn.esprit._4se2.pi.entities.CommunityPost;

import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    Page<CommunityPost> findByPostTypeNotAndStatusOrderByCreatedAtDesc(
            PostType excludedType, PostStatus status, Pageable pageable);

    List<CommunityPost> findByTeamIdAndStatusOrderByCreatedAtDesc(Long teamId, PostStatus status);

    List<CommunityPost> findByTeamSportAndStatusOrderByCreatedAtDesc(String sport, PostStatus status);

    List<CommunityPost> findByCommunityIdAndStatusOrderByCreatedAtDesc(Long communityId, PostStatus status);
}
