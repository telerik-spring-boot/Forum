package com.telerik.forum.models.dtos.postDTOs;

import com.telerik.forum.models.dtos.commentDTOs.CommentDisplayDTO;

import java.util.List;

public class PostDisplayDTO {

    public String creatorUsername;

    public String title;

    public String content;

    public List<CommentDisplayDTO> comments;

    public PostDisplayDTO() {
    }

    public PostDisplayDTO(String creatorUsername, String title, String content, List<CommentDisplayDTO> comments) {
        this.creatorUsername = creatorUsername;
        this.title = title;
        this.content = content;
        this.comments = comments;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<CommentDisplayDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDisplayDTO> comments) {
        this.comments = comments;
    }
}
