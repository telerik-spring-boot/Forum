package com.telerik.forum.services;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.Comment;
import com.telerik.forum.models.Post;
import com.telerik.forum.models.User;
import com.telerik.forum.repositories.AdminRepository;
import com.telerik.forum.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.telerik.forum.services.PostServiceImpl.BLOCKED_ACCOUNT_MESSAGE;

@Service
public class CommentServiceImpl implements CommentService {
    private static final String UNAUTHORIZED_UPDATE_MESSAGE = "You do not have permission to update this comment!";
    private static final String UNAUTHORIZED_DELETE_MESSAGE = "You do not have permission to delete this comment!";
    private final CommentRepository commentRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, AdminRepository adminRepository) {
        this.commentRepository = commentRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public Comment getComment(int id) {
        Comment comment = commentRepository.getById(id);
        if (comment == null) {
            throw new EntityNotFoundException("Comment", "id", id);
        }
        return comment;
    }

    @Override
    public void addComment(Post post, Comment comment, User user) {
        comment.setPost(post);
        comment.setUser(user);
        commentRepository.create(comment);
        //postRepository.update(post);

    }

    @Override
    public void updateComment(Post post, Comment comment, User user) {
        checkCommentUpdatePermission(comment.getId(), user);
        commentRepository.update(comment);
        //postRepository.update(post);// Do we need this with CascadeType.ALL?
    }

    @Override
    public void deleteComment(Post post, Comment comment, User user) {
        checkCommentDeletePermission(comment.getId(), user);
        commentRepository.delete(comment);
        //postRepository.update(post);
    }

    private void checkCommentUpdatePermission(int commentId, User user) {
        Comment comment = getComment(commentId);
        if (!comment.getUser().equals(user)) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_UPDATE_MESSAGE);
        }
        if (user.isBlocked()) {
            throw new UnauthorizedOperationException(BLOCKED_ACCOUNT_MESSAGE);
        }
    }

    private void checkCommentDeletePermission(int commentId, User user) {
        Comment comment = getComment(commentId);

        boolean isAdmin = adminRepository.getByUserId(user.getId()) != null;
        if (!(comment.getUser().equals(user) || isAdmin)) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_DELETE_MESSAGE);
        }
        if (user.isBlocked()) {
            throw new UnauthorizedOperationException(BLOCKED_ACCOUNT_MESSAGE);
        }
    }

}
