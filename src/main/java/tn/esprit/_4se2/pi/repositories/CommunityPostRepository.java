package tn.esprit._4se2.pi.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit._4se2.pi.entities.CommunityPost;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    Page<CommunityPost> findByTeamIdOrderByCreatedAtDesc(Long teamId, Pageable pageable);
}
