package com.telerik.forum.repositories.post;

import com.telerik.forum.models.dtos.postDTOs.PostDisplayMvcDTO;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.filters.FilterPostOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepository {

    List<Post> getAllPosts();

    Page<Post> getAllPostsWithFilters(FilterPostOptions options, Pageable pageable);

    List<Post> getAllPostsWithFilters(FilterPostOptions options);

    List<Post> getMostCommentedPosts(int limit);

    List<Post> getMostLikedPosts(int limit);

    List<Post> getMostRecentPosts(int limit);

    Page<Post> getPostsWithCommentsByUserId(int userId, FilterPostOptions options, Pageable pageable);

    List<PostDisplayMvcDTO> getPostsCreationDates();

    Post getPostById(int id);

    Post getPostWithCommentsById(int id);

    Post getPostWithLikesById(int id);

    Post getPostWithTagsById(int id);

    Post getPostWithCommentsAndLikesAndTagsById(int id);

    void create(Post post);

    void update(Post post);

    void delete(int id);
}
