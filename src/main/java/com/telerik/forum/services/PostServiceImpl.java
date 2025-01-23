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

    @Override
    public List<Post> getPostsByAuthor(User user) {
        return postRepository.getbyUserId(user.getId());
    }

    @Override
    public Post getPost(int id) {
        return postRepository.getById(id);
    }

    @Override
    public Post addComment(Post post, Comment comment, User user) {
        comment.setPost(post);
        comment.setUser(user);
        commentRepository.create(comment);
        postRepository.update(post);
        return post;
    }

    @Override
    public void createPost(Post post, User user) {
        post.setUser(user);
        postRepository.create(post);
    }

    @Override
    public void updatePost(Post post, User user) {
        checkUpdatePermission(post.getId(), user);
        postRepository.update(post);
    }

    @Override
    public void deletePost(int postId, User user) {
        checkDeletePermission(postId, user);
        postRepository.delete(postId);
    }

    private void checkUpdatePermission(int postId, User user) {
        Post post = postRepository.getById(postId);
        if (!post.getUser().equals(user)) {
            throw new UnauthorizedOperationException("You do not have permission to update this post!");
        }
    }

    private void checkDeletePermission(int postId, User user) {
        Post post = postRepository.getById(postId);
        try {
            adminRepository.getByUserId(user.getId());
        } catch (EntityNotFoundException e) {
            if (!post.getUser().equals(user)) {
                throw new UnauthorizedOperationException("You do not have permission to update this post!");
            }
        }
    }

    // TODO restrict the capabilities of a blocked user
}
