package com.telerik.forum.models.dtos;

import com.telerik.forum.models.dtos.postDTOs.PostDisplayDTO;
import com.telerik.forum.models.post.Comment;
import com.telerik.forum.models.post.Post;

import java.time.LocalDateTime;

public class PostCommentWrapper {

    LocalDateTime createdAt;

    private Comment comment;

    private PostDisplayDTO post;


    public PostCommentWrapper() {
    }

    public PostCommentWrapper(Comment comment) {
        this.comment = comment;
        this.createdAt = comment.getCreatedAt();
    }

    public PostCommentWrapper(PostDisplayDTO post) {
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

    public PostDisplayDTO getPost() {
        return post;
    }

    public void setPost(PostDisplayDTO post) {
        this.post = post;
    }
}
