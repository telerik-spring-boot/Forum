package com.telerik.forum.services.comment;

import com.telerik.forum.models.post.Comment;
import com.telerik.forum.models.user.User;

public interface CommentService {

    Comment getComment(int id);

//    List<Comment> getByPostId(int postId);

    void addComment(int postId, Comment comment, User user);

    void updateComment( Comment comment, User user);

    void deleteComment(int postId, int commentId, User user);

}
