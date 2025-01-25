package com.telerik.forum.services;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.Post;
import com.telerik.forum.models.User;
import com.telerik.forum.repositories.AdminRepositoryOld;
import com.telerik.forum.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final AdminRepositoryOld adminRepositoryOld;

    @Autowired
    public PostServiceImpl(PostRepository postRepository,
                           AdminRepositoryOld adminRepositoryOld) {
        this.postRepository = postRepository;
        this.adminRepositoryOld = adminRepositoryOld;
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
        boolean isAdmin = adminRepositoryOld.getByUserId(user.getId()) != null;
        if (!(post.getUser().equals(user) || isAdmin)) {
            throw new UnauthorizedOperationException("You do not have permission to delete this post!");
        }

    }

    // TODO restrict the capabilities of a blocked user
}
