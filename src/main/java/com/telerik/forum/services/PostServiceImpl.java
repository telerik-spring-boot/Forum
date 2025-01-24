package com.telerik.forum.services;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.Comment;
import com.telerik.forum.models.Post;
import com.telerik.forum.models.User;
import com.telerik.forum.repositories.AdminRepository;
import com.telerik.forum.repositories.CommentRepository;
import com.telerik.forum.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, CommentRepository commentRepository,
                           AdminRepository adminRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public List<Post> getPosts() {
        return postRepository.getAll();
    }

//    @Override
//    public List<Post> getPostsByAuthor(User user) {
//        return postRepository.getPostsAndCommentsbyUserId(user.getId());
//    }

    @Override
    public Post getPost(int id) {
        Post post = postRepository.getPostById(id);
        if (post == null) {
            throw new EntityNotFoundException("Post", "id", id);
        }
        return post;
    }

    @Override
    public void addComment(Post post, Comment comment, User user) {
        comment.setPost(post);
        comment.setUser(user);
        commentRepository.create(comment);
        postRepository.update(post);

    }

    @Override
    public void updateComment(Post post, Comment comment, User user) {
        checkCommentUpdatePermission(comment.getId(), user);
        commentRepository.update(comment);
        postRepository.update(post);// Do we need this with CascadeType.ALL?
    }

    @Override
    public void deleteComment(Post post, Comment comment, User user) {
        checkCommentDeletePermission(comment.getId(), user);
        commentRepository.delete(comment);
        postRepository.update(post);
    }

    @Override
    public void createPost(Post post, User user) {
        post.setUser(user);
        postRepository.create(post);
    }

    @Override
    public void updatePost(Post post, User user) {
        checkPostUpdatePermission(post.getId(), user);
        postRepository.update(post);
    }

    @Override
    public void deletePost(int postId, User user) {
        checkPostDeletePermission(postId, user);
        postRepository.delete(postId);
    }

    private void checkPostUpdatePermission(int postId, User user) {
        Post post = postRepository.getPostById(postId);

        if (!post.getUser().equals(user)) {
            throw new UnauthorizedOperationException("You do not have permission to update this post!");
        }
    }

    private void checkPostDeletePermission(int postId, User user) {
        Post post = postRepository.getPostById(postId);
        if (post == null) {
            throw new EntityNotFoundException("Post", "id", postId);
        }
        boolean isAdmin = adminRepository.getByUserId(user.getId()) != null;
        if (!(post.getUser().equals(user) || isAdmin)) {
            throw new UnauthorizedOperationException("You do not have permission to delete this post!");
        }

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

    // TODO restrict the capabilities of a blocked user
}
