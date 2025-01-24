package com.telerik.forum.models.dtos;

public class CommentDisplayDTO {


    private String creatorUsername;

    private String commentContent;

    public CommentDisplayDTO() {
    }

    public CommentDisplayDTO(String creatorUsername, String commentContent) {
        this.creatorUsername = creatorUsername;
        this.commentContent = commentContent;
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
}
