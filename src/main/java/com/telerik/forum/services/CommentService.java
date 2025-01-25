package com.telerik.forum.services;

import com.telerik.forum.models.Comment;
import com.telerik.forum.models.Post;
import com.telerik.forum.models.User;

import java.util.List;

public interface CommentService {

    Comment getComment(int id);

//    List<Comment> getByPostId(int postId);

    void addComment(Post post, Comment comment, User user);

    void updateComment(Post post, Comment comment, User user);

    void deleteComment(Post post, int commentId, User user);

}
