package com.telerik.forum.services;

import com.telerik.forum.models.Comment;
import com.telerik.forum.models.Post;
import com.telerik.forum.models.User;

import java.util.List;

public interface PostService {

    List<Post> getPosts();

    List<Post> getPostsByAuthor(User user);

    Post getPost(int id);

    void addComment(Post post, Comment comment, User user);

    void updateComment(Post post, Comment comment, User user);

    void deleteComment(Post post, Comment comment, User user);

    void createPost(Post post, User user);

    void updatePost(Post post, User user);

    void deletePost(int postId, User user);
}
