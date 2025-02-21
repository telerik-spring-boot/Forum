package com.telerik.forum.models.dtos.commentDTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

public class CommentDisplayDTO {


    private String creatorUsername;

    private String commentContent;

    @JsonIgnore
    public int creatorId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm '['dd-MM-yyyy']'")
    public LocalDateTime createdAt;

    public CommentDisplayDTO() {
    }

    public CommentDisplayDTO(String creatorUsername, String commentContent, int creatorId, LocalDateTime createdAt) {
        this.creatorUsername = creatorUsername;
        this.commentContent = commentContent;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
