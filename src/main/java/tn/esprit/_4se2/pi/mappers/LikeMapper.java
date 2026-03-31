package tn.esprit._4se2.pi.mappers;

import tn.esprit._4se2.pi.dto.LikeRequest;
import tn.esprit._4se2.pi.dto.LikeResponse;
import tn.esprit._4se2.pi.entities.Like;

public class LikeMapper {
    public static Like toEntity(LikeRequest dto) {
        Like like = new Like();
        return like;
    }

    public static LikeResponse toDto(Like like) {
        LikeResponse dto = new LikeResponse();
        dto.setId(like.getId());
        return dto;
    }

}
