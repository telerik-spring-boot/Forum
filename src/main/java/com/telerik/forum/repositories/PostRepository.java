package com.telerik.forum.repositories;

import com.telerik.forum.models.Post;

import java.util.List;

public interface PostRepository {

    List<Post> getAll();

    List<Post> getMostCommentedPosts(int limit);

    List<Post> getMostLikedPosts(int limit);

    Post getPostById(int id);

    Post getPostWithCommentsById(int id);

    Post getPostWithLikesById(int id);

    Post getPostWithTagsById(int id);

    Post getPostWithCommentsAndLikesAndTagsById(int id);

   // List<Post> getPostsAndCommentsbyUserId(int userId);

    void create(Post post);

    void update(Post post);

    void delete(int id);
}
