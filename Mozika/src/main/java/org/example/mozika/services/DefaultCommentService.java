package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.Comment;
import org.example.mozika.models.dto.CommentSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.CommentRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.CommentService;
import org.example.mozika.specification.CommentSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultCommentService implements CommentService {
	private final CommentRepository commentRepository;

	public DefaultCommentService(CommentRepository commentRepository) {
	   this.commentRepository = commentRepository;
	}

	@Override
	public String exportCommentToCSV(List<Comment> comment) {
	   return ExportUtils.generateCsv(comment);
	}  

	@Override
	public Page<Comment> getAllComment(Pageable pageable) {
	    try {
	        return commentRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving comment", ex);
	    }
	}

	@Override
	public Page<Comment> getAllComment(Pageable pageable, CommentSearch object) {
	    try {
	        Specification<Comment> spec=CommentSpecification.filter(object);
	        return commentRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving comment", ex);
	    }
	}

	@Override
	public Comment getCommentById(Long id) {
	    Optional<Comment> comment = commentRepository.findById(id);
	    if (comment.isPresent()) {
	        return comment.get();
	    } else {
	        throw new ResourceNotFoundException("Comment not found with id : " + id);
	    }
	}

	@Override
	public Comment createComment(Comment comment) {
	    try {
	        return commentRepository.save(comment);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating comment", ex);
	    }
	}

	@Override
	public Comment updateComment(Long id, Comment comment) {
	    Optional<Comment> existingComment = commentRepository.findById(id);
	    if (existingComment.isPresent()) {
	        comment.setId(id);
	        try {
	            return commentRepository.save(comment);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating comment", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("Comment not found with id : " + id);
	    }
	}

	@Override
	public void deleteComment(Long id) {
	    try {
	        commentRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting comment", ex);
	    }
	}



}
