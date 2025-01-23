package com.telerik.forum.repositories;

import com.telerik.forum.models.Post;

import java.util.List;

public interface PostRepository {

    List<Post> getAll();

    Post getById(int id);

    List<Post> getbyUserId(int userId);

    void create(Post post);

    void update(Post post);

    void delete(int id);
}
