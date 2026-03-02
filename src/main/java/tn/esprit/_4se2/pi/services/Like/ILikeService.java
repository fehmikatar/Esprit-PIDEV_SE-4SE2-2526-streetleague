package tn.esprit._4se2.pi.services.Like;

import tn.esprit._4se2.pi.entities.Like;
import java.util.List;

public interface ILikeService {
    Like addLike(Like like);
    List<Like> getAllLikes();
    Like getLikeById(Long id);
    void deleteLike(Long id);
}