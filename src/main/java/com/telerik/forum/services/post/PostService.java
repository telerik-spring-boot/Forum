package com.telerik.forum.services.post;

import com.telerik.forum.models.dtos.postDTOs.PostDisplayDTO;
import com.telerik.forum.models.filters.FilterPostOptions;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {

    List<Post> getAllPosts();

    Page<Post> getAllPostsWithFilters(FilterPostOptions filterPostOptions, Pageable pageable);

    List<Post> getAllPostsWithFilters(FilterPostOptions filterPostOptions);

    List<Post> getMostCommentedPosts(int limit);

    List<Post> getMostLikedPosts(int limit);

    List<Post> getMostRecentPosts(int limit);

    List<PostDisplayDTO> getPostsCreationDates();

    Post getById(int id);

    Post getByIdWithComments(int id);

    Post getByIdWithLikes(int id);

    Post getByIdWithCommentsAndLikesAndTags(int id);

    void createPost(Post post, User user);

    void updatePost(Post post, User user);

    void deletePost(int postId, User user);

}
