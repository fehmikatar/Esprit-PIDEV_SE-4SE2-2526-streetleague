package tn.esprit._4se2.pi.services.Comment;

import tn.esprit._4se2.pi.entities.Comment;

import java.util.List;

public interface ICommentService {
    Comment addComment(Comment comment);

    List<Comment> getAllComments();

    Comment getCommentById(Long id);

    Comment updateComment(Comment comment);

    void deleteComment(Long id);
}
