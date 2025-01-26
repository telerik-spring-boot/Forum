package com.telerik.forum.repositories;

import com.telerik.forum.models.Post;

import java.util.List;

public interface PostRepository {

    List<Post> getAll();

    Post getPostById(int id);

    Post getPostAndCommentsById(int id);

    Post getPostAndLikesById(int id);

    Post getPostAndCommentsAndLikesById(int id);

   // List<Post> getPostsAndCommentsbyUserId(int userId);

    void create(Post post);

    void update(Post post);

    void delete(int id);
}
