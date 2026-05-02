package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.CommunityMember;

import java.util.List;

@Repository
public interface CommunityMemberRepository extends JpaRepository<CommunityMember, Long> {

    boolean existsByCommunityIdAndUserId(Long communityId, Long userId);

    List<CommunityMember> findAllByUserIdOrderByJoinedAtDesc(Long userId);

    List<CommunityMember> findAllByCommunityIdOrderByJoinedAtAsc(Long communityId);
}
