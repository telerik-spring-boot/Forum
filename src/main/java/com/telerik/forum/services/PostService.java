package com.telerik.forum.services;

import com.telerik.forum.models.Post;
import com.telerik.forum.models.User;

import java.util.List;

public interface PostService {

    List<Post> getPosts();

    //List<Post> getPostsByAuthor(User user);

    Post getById(int id);

    Post getByIdWithComments(int id);

    Post getByIdWithLikes(int id);

    Post getByIdWithCommentsAndLikes(int id);

    void createPost(Post post, User user);

    void updatePost(Post post, User user);

    void deletePost(int postId, User user);

}
