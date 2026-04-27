package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< Updated upstream
import org.springframework.stereotype.Repository;
=======
>>>>>>> Stashed changes
import tn.esprit._4se2.pi.entities.CommunityMember;

import java.util.List;

<<<<<<< Updated upstream
@Repository
public interface CommunityMemberRepository extends JpaRepository<CommunityMember, Long> {

    boolean existsByCommunityIdAndUserId(Long communityId, Long userId);

    List<CommunityMember> findAllByUserIdOrderByJoinedAtDesc(Long userId);

    List<CommunityMember> findAllByCommunityIdOrderByJoinedAtAsc(Long communityId);
}
=======
public interface CommunityMemberRepository extends JpaRepository<CommunityMember, Long> {
    boolean existsByCommunity_IdAndUser_Id(Long communityId, Long userId);
    List<CommunityMember> findByUser_Id(Long userId);
}
>>>>>>> Stashed changes
