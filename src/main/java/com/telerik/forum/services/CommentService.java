package com.telerik.forum.services;

import com.telerik.forum.models.Comment;
import com.telerik.forum.models.Post;
import com.telerik.forum.models.User;

import java.util.List;

public interface CommentService {

    Comment getComment(int id);

//    List<Comment> getByPostId(int postId);

    void addComment(int postId, Comment comment, User user);

    void updateComment( Comment comment, User user);

    void deleteComment(int postId, int commentId, User user);

}
