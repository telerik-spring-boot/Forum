package com.telerik.forum.services.post;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.post.Like;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.user.User;
import com.telerik.forum.models.filters.FilterPostOptions;
import com.telerik.forum.repositories.admin.AdminDetailsRepository;
import com.telerik.forum.repositories.post.PostRepository;
import com.telerik.forum.repositories.utilities.SortingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    public static final String BLOCKED_ACCOUNT_MESSAGE = "Your account is blocked!";
    private static final String UNAUTHORIZED_DELETE_MESSAGE = "You do not have permission to delete this post!";
    private static final String UNAUTHORIZED_UPDATE_MESSAGE = "You do not have permission to update this post!";

    private final PostRepository postRepository;
    private final AdminDetailsRepository adminRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository,
                           AdminDetailsRepository adminRepository) {
        this.postRepository = postRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public List<Post> getAllPosts() {
        List<Post> posts = postRepository.getAllPosts();

//        if(posts.isEmpty()){
//            throw new EntityNotFoundException("There are no posts.");
//        }

        return postRepository.getAllPosts();
    }

    @Override
    public List<Post> getAllPostsWithFilters(FilterPostOptions filterOptions) {

        filterOptions.getSortBy().ifPresent(SortingHelper::validateSortByFieldPost);

        filterOptions.getSortOrder().ifPresent(SortingHelper::validateSortOrderField);

        List<Post> posts = postRepository.getAllPostsWithFilters(filterOptions);
        filterByLikes(posts, filterOptions);

        return posts;

    }

    @Override
    public List<Post> getMostCommentedPosts(int limit) {
        return postRepository.getMostCommentedPosts(limit);
    }

    @Override
    public List<Post> getMostLikedPosts(int limit) {
        return postRepository.getMostLikedPosts(limit);
    }


    @Override
    public Post getById(int id) {
        Post post = postRepository.getPostById(id);

        if (post == null) {
            throw new EntityNotFoundException("Post", "id", id);
        }

        return post;
    }

    @Override
    public Post getByIdWithComments(int id) {
        Post post = postRepository.getPostWithCommentsById(id);

        if (post == null) {
            throw new EntityNotFoundException("Post", "id", id);
        }

        return post;
    }

    @Override
    public Post getByIdWithLikes(int id) {
        Post post = postRepository.getPostWithLikesById(id);

        if (post == null) {
            throw new EntityNotFoundException("Post", "id", id);
        }

        return post;
    }

    @Override
    public Post getByIdWithCommentsAndLikes(int id) {
        Post post = postRepository.getPostWithCommentsAndLikesAndTagsById(id);

        if (post == null) {
            throw new EntityNotFoundException("Post", "id", id);
        }

        return post;
    }

    @Override
    public void createPost(Post post, User user) {
        checkPostCreatePermission(user);
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

    private void checkPostCreatePermission(User user) {
        if (user.isBlocked()) {
            throw new UnauthorizedOperationException(BLOCKED_ACCOUNT_MESSAGE);
        }
    }


    private void checkPostUpdatePermission(int postId, User user) {
        Post post = getById(postId);

        if (!post.getUser().equals(user)) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_UPDATE_MESSAGE);
        }
        if (user.isBlocked()) {
            throw new UnauthorizedOperationException(BLOCKED_ACCOUNT_MESSAGE);
        }
    }

    private void checkPostDeletePermission(int postId, User user) {
        Post post = getById(postId);

        boolean isAdmin = adminRepository.getByUserId(user.getId()) != null;
        if (!(post.getUser().equals(user) || isAdmin)) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_DELETE_MESSAGE);
        }
        if (user.isBlocked()) {
            throw new UnauthorizedOperationException(BLOCKED_ACCOUNT_MESSAGE);
        }

    }

    private void filterByLikes(List<Post> posts, FilterPostOptions options) {
        List<Post> postsToDelete = new ArrayList<>();

        for (Post post : posts) {
            options.getMinLikes().ifPresent(minLikes -> {
                if (post.getLikes().stream()
                        .map(Like::getReaction)
                        .mapToInt(Integer::intValue)
                        .sum() < minLikes) {
                    postsToDelete.add(post);
                }
            });

            options.getMaxLikes().ifPresent(maxLikes -> {
                if (post.getLikes().stream()
                        .map(Like::getReaction)
                        .mapToInt(Integer::intValue)
                        .sum() > maxLikes) {
                    postsToDelete.add(post);
                }
            });
        }
        if (!postsToDelete.isEmpty()) {
            posts.removeAll(postsToDelete);
        }
    }


}
