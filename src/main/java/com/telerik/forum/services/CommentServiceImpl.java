package com.telerik.forum.services;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.Comment;
import com.telerik.forum.models.Post;
import com.telerik.forum.models.User;
import com.telerik.forum.repositories.AdminRepositoryOld;
import com.telerik.forum.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.telerik.forum.services.PostServiceImpl.BLOCKED_ACCOUNT_MESSAGE;

@Service
public class CommentServiceImpl implements CommentService {
    private static final String UNAUTHORIZED_UPDATE_MESSAGE = "You do not have permission to update this comment!";
    private static final String UNAUTHORIZED_DELETE_MESSAGE = "You do not have permission to delete this comment!";

    private final CommentRepository commentRepository;
    private final AdminRepositoryOld adminRepository;


    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, AdminRepositoryOld adminRepository) {
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
    public List<Comment> getByPostId(int postId) {
        return commentRepository.getByPostId(postId);
    }

    @Override
    public void addComment(Post post, Comment comment, User user) {
        comment.setPost(post);
        comment.setUser(user);
        post.getComments().add(comment);
        commentRepository.create(comment);

    }

    @Override
    public void updateComment(Post post, Comment comment, User user) {

        checkCommentUpdatePermission(comment.getId(), user);
        commentRepository.update(comment);

    }

    @Override
    public void deleteComment(Post post, int commentId, User user) {
        int commentSize = post.getComments().size();
        if (commentId > commentSize) {
            throw new EntityNotFoundException("Comment", "id", commentId);
        }
        Comment commentToDelete = post.getComments().get(commentId - 1);
        checkCommentDeletePermission(commentToDelete.getId(), user);
        commentRepository.delete(commentToDelete.getId());
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
