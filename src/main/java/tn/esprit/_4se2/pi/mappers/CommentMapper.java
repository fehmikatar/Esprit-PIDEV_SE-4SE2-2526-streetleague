package tn.esprit._4se2.pi.mappers;

import tn.esprit._4se2.pi.dto.CommentRequest;
import tn.esprit._4se2.pi.dto.CommentResponse;
import tn.esprit._4se2.pi.entities.Comment;
import tn.esprit._4se2.pi.entities.Post;
import tn.esprit._4se2.pi.entities.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static Comment toEntity(CommentRequest dto, Post post, User user) {
        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setPost(post);
        comment.setAuthor(user);
        comment.setCreatedAt(LocalDateTime.now());
        return comment;
    }

    public static CommentResponse toDto(Comment comment) {
        CommentResponse dto = new CommentResponse();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        return dto;
    }
}
