package org.example.mozika.services.interfaces;

import org.example.mozika.models.Comment;
import org.example.mozika.models.dto.CommentSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface CommentService {
    Page<Comment> getAllComment(Pageable pageable);

    Page<Comment> getAllComment(Pageable pageable, CommentSearch object);

    Comment getCommentById(Long id);

    public String exportCommentToCSV(List<Comment> comment);

    

    Comment createComment(Comment comment);

    Comment updateComment(Long id, Comment comment);

    void deleteComment(Long id);
    

}
