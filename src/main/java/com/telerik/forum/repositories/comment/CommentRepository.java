package com.telerik.forum.repositories.comment;

import com.telerik.forum.models.post.Comment;
import com.telerik.forum.models.filters.FilterCommentOptions;

import java.util.List;

public interface CommentRepository {

    //    List<Comment> getAll();
//
     Comment getById(int id);
//
  //   List<Comment> getByPostId(int postId);

    List<Comment> getByUserId(int id, FilterCommentOptions options);

    List<Comment> getWithFilters(FilterCommentOptions options);

    void create(Comment comment);

    void update(Comment comment);

    void delete(int commentId);

}
