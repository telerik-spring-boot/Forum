package com.telerik.forum.services;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.Post;
import com.telerik.forum.models.User;
import com.telerik.forum.repositories.AdminRepository;
import com.telerik.forum.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private static final String BLOCKED_ACCOUNT_MESSAGE = "Your account is blocked!";
    private static final String UNAUTHORIZED_DELETE_MESSAGE = "You do not have permission to delete this post!";
    private static final String UNAUTHORIZED_UPDATE_MESSAGE = "You do not have permission to update this post!";

    private final PostRepository postRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository,
                           AdminRepository adminRepository) {
        this.postRepository = postRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public List<Post> getPosts() {
        return postRepository.getAll();
    }


    @Override
    public Post getPost(int id) {
        Post post = postRepository.getPostById(id);
        if (post == null) {
            throw new EntityNotFoundException("Post", "id", id);
        }
        return post;
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
        Post post = getPost(postId);

        if (!post.getUser().equals(user)) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_UPDATE_MESSAGE);
        }
        if (user.isBlocked()) {
            throw new UnauthorizedOperationException(BLOCKED_ACCOUNT_MESSAGE);
        }
    }

    private void checkPostDeletePermission(int postId, User user) {
        Post post = getPost(postId);

        boolean isAdmin = adminRepository.getByUserId(user.getId()) != null;
        if (!(post.getUser().equals(user) || isAdmin)) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_DELETE_MESSAGE);
        }
        if (user.isBlocked()) {
            throw new UnauthorizedOperationException(BLOCKED_ACCOUNT_MESSAGE);
        }

    }


}
