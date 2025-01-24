package com.telerik.forum.services;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.Comment;
import com.telerik.forum.models.Post;
import com.telerik.forum.models.User;
import com.telerik.forum.repositories.AdminRepository;
import com.telerik.forum.repositories.CommentRepository;

public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final AdminRepository adminRepository;

    public CommentServiceImpl(CommentRepository commentRepository, AdminRepository adminRepository) {
        this.commentRepository = commentRepository;
        this.adminRepository = adminRepository;
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
        Comment comment = commentRepository.getById(commentId);
        if (!comment.getUser().equals(user)) {
            throw new UnauthorizedOperationException("You do not have permission to update this comment!");
        }
    }

    private void checkCommentDeletePermission(int commentId, User user) {
        Comment comment = commentRepository.getById(commentId);
        if (comment == null) {
            throw new EntityNotFoundException("Comment", "id", commentId);
        }
        boolean isAdmin = adminRepository.getByUserId(user.getId()) != null;
        if (!(comment.getUser().equals(user) || isAdmin)) {
            throw new UnauthorizedOperationException("You do not have permission to delete this comment!");
        }


    }

}
