package com.telerik.forum.repositories;

import com.telerik.forum.models.Comment;
import com.telerik.forum.models.filters.FilterCommentOptions;

import java.util.List;

public interface CommentRepository {

    //    List<Comment> getAll();
//
     Comment getById(int id);
//
  //   List<Comment> getByPostId(int postId);

    List<Comment> getByUserId(int id, FilterCommentOptions options);

    void create(Comment comment);

    void update(Comment comment);

    void delete(int commentId);

}
