package com.telerik.forum.services.comment;

import com.telerik.forum.models.filters.FilterCommentOptions;
import com.telerik.forum.models.post.Comment;
import com.telerik.forum.models.user.User;

import java.util.List;

public interface CommentService {

    Comment getComment(int id);

    List<Comment> getAllComments(FilterCommentOptions options);

    void addComment(int postId, Comment comment, User user);

    void updateComment( Comment comment, User user);

    void deleteComment(int postId, int commentId, User user);

}
