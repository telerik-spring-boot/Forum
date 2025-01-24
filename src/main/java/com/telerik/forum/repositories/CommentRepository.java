package com.telerik.forum.repositories;

import com.telerik.forum.models.Comment;

import java.util.List;

public interface CommentRepository {

    //    List<Comment> getAll();
//
    Comment getById(int id);
//
//    List<Comment> getbyPostId(int postId);

    void create(Comment comment);

    void update(Comment comment);

    void delete(Comment comment);

}
