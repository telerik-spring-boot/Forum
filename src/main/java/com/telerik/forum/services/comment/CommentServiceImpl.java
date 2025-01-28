package com.telerik.forum.services.comment;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.post.Comment;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.user.User;
import com.telerik.forum.repositories.admin.AdminDetailsRepository;
import com.telerik.forum.repositories.comment.CommentRepository;
import com.telerik.forum.repositories.post.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import static com.telerik.forum.services.post.PostServiceImpl.BLOCKED_ACCOUNT_MESSAGE;

@Service
public class CommentServiceImpl implements CommentService {
    private static final String UNAUTHORIZED_UPDATE_MESSAGE = "You do not have permission to update this comment!";
    private static final String UNAUTHORIZED_DELETE_MESSAGE = "You do not have permission to delete this comment!";

    private final CommentRepository commentRepository;
    private final AdminDetailsRepository adminRepository;
    private final PostRepository postRepository;


    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, AdminDetailsRepository adminRepository,
                              PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.adminRepository = adminRepository;
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
    public void addComment(int postId, Comment comment, User user) {
        Post post = postRepository.getPostWithCommentsById(postId);
        // not needed to fetch comments, only post is sufficient !

        if(post == null){
            throw new EntityNotFoundException("Post", "id", postId);
        } // included

        comment.setPost(post);
        comment.setUser(user);
        // post.getComments().add(comment); REDUNDANT

        commentRepository.create(comment);

    }

    @Override
    public void updateComment( Comment comment, User user) {

        // --> to be checked for more efficient way// we already have the comment, why fetching from the database again
        checkCommentUpdatePermission(comment.getId(), user);

        commentRepository.update(comment);

    }


    // would variable renaming be good? commentId represents the ID and is actually the number of comment in postList
    @Override
    public void deleteComment(int postId, int commentId, User user) {
        Post post = postRepository.getPostWithCommentsById(postId);

        if(post == null ){
            throw new EntityNotFoundException("Post", "id", postId);
        } // included

        int commentSize = post.getComments().size();

        if (commentId > commentSize) {
            throw new EntityNotFoundException("Comment", "id", commentId);
        }


        Comment commentToDelete = post.getComments().stream()
                .toList().get(commentId - 1);

        // is it necessary to fetch from the database if we already have the comment
        checkCommentDeletePermission(commentToDelete.getId(), user);

        commentRepository.delete(commentToDelete.getId());
    }

    private void checkCommentUpdatePermission(int commentId, User user) {
        // this can be fixed so we do not make redundant call to the database
        Comment comment = getComment(commentId);

        if (!comment.getUser().equals(user)) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_UPDATE_MESSAGE);
        }

        if (user.isBlocked()) {
            throw new UnauthorizedOperationException(BLOCKED_ACCOUNT_MESSAGE);
        }
    }

    private void checkCommentDeletePermission(int commentId, User user) {
        // this can be fixed so we do not make redundant call to the database
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
