package com.telerik.forum.services;

import com.telerik.forum.models.Comment;
import com.telerik.forum.models.Post;
import com.telerik.forum.models.User;

import java.util.List;

public interface PostService {

    List<Post> getPosts();

    //List<Post> getPostsByAuthor(User user);

    Post getPost(int id);

    void createPost(Post post, User user);

    void updatePost(Post post, User user);

    void deletePost(int postId, User user);

    void likePost(Post post);

    void dislikePost(Post post);
}
