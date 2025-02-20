package com.telerik.forum.models.dtos;

import com.telerik.forum.models.post.Comment;
import com.telerik.forum.models.post.Post;

import java.time.LocalDateTime;

public class PostCommentWrapper {

    LocalDateTime createdAt;

    private Comment comment;

    private Post post;


    public PostCommentWrapper() {
    }

    public PostCommentWrapper(Comment comment) {
        this.comment = comment;
        this.createdAt = comment.getCreatedAt();
    }

    public PostCommentWrapper(Post post) {
        this.post = post;
        this.createdAt = post.getCreatedAt();
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
