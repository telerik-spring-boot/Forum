package com.telerik.forum.repositories;

import com.telerik.forum.models.Post;

import java.util.List;

public interface PostRepository {

    List<Post> getAll();

    Post getPostById(int id);

    //Post getPostAndCommnetsById(int id);

   // List<Post> getPostsAndCommentsbyUserId(int userId);

    void create(Post post);

    void update(Post post);

    void delete(int id);
}
