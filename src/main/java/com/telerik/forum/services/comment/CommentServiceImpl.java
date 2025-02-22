package com.telerik.forum.services.comment;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.filters.FilterCommentOptions;
import com.telerik.forum.models.post.Comment;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.user.User;
import com.telerik.forum.repositories.comment.CommentRepository;
import com.telerik.forum.repositories.post.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.telerik.forum.services.post.PostServiceImpl.BLOCKED_ACCOUNT_MESSAGE;

@Service
public class CommentServiceImpl implements CommentService {
    private static final String UNAUTHORIZED_UPDATE_MESSAGE = "You do not have permission to update this comment!";
    private static final String UNAUTHORIZED_DELETE_MESSAGE = "You do not have permission to delete this comment!";

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;


    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository,
                              PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;

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
    public List<Comment> getAllComments(FilterCommentOptions options) {
        return commentRepository.getWithFilters(options);
    }

    @Override
    public void addComment(int postId, Comment comment, User user) {
        checkCommentCreatePermission(user);

        Post post = postRepository.getPostWithCommentsById(postId);

        if (post == null) {
            throw new EntityNotFoundException("Post", "id", postId);
        }

        comment.setPost(post);
        comment.setUser(user);

        commentRepository.create(comment);

    }

    @Override
    public void updateComment(Comment comment, User user) {

        checkCommentUpdatePermission(comment, user);

        commentRepository.update(comment);

    }

    @Override
    public void deleteComment(int postId, int commentId, User user) {
        Comment commentToDelete = commentRepository.getById(commentId);

        if (commentToDelete == null) {
            throw new EntityNotFoundException("Post", "id", postId);
        }

        if (commentToDelete.getPost().getId() != postId) {
            throw new EntityNotFoundException("Comment with id: " + commentId + " not found for post with id:  " + postId);

        }

        checkCommentDeletePermission(commentToDelete, user);

        commentRepository.delete(commentToDelete.getId());
    }

    private void checkCommentCreatePermission(User user) {
        if (user.isBlocked()) {
            throw new UnauthorizedOperationException(BLOCKED_ACCOUNT_MESSAGE);
        }
    }

    private void checkCommentUpdatePermission(Comment comment, User user) {
        if (!comment.getUser().equals(user)) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_UPDATE_MESSAGE);
        }

        if (user.isBlocked()) {
            throw new UnauthorizedOperationException(BLOCKED_ACCOUNT_MESSAGE);
        }
    }

    private void checkCommentDeletePermission(Comment comment, User user) {
        boolean isAdmin = checkIfUserIsAdmin(user);

        if (!(comment.getUser().equals(user) || isAdmin)) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_DELETE_MESSAGE);
        }

        if (user.isBlocked()) {
            throw new UnauthorizedOperationException(BLOCKED_ACCOUNT_MESSAGE);
        }
    }

    private static boolean checkIfUserIsAdmin(User user) {
        return user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"));
    }

}
