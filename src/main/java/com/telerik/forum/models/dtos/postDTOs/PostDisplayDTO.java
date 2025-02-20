package com.telerik.forum.models.dtos.postDTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.telerik.forum.models.dtos.commentDTOs.CommentDisplayDTO;

import java.time.LocalDateTime;
import java.util.List;

public class PostDisplayDTO {

    @JsonIgnore
    public int id;

    @JsonIgnore
    public int userId;

    @JsonIgnore
    public int reaction;


    public String creatorUsername;

    public String title;

    public String content;

    public List<String> tags;

    public int likes;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm '['dd-MM-yyyy']'")
    public LocalDateTime createdAt;

    public List<CommentDisplayDTO> comments;

    public PostDisplayDTO() {
    }

    public PostDisplayDTO(int userId, String creatorUsername, String title,
                          String content, List<String> tags,
                          int likes, LocalDateTime createdAt,
                          List<CommentDisplayDTO> comments) {
        this.creatorUsername = creatorUsername;
        this.title = title;
        this.content = content;
        this.tags = tags;
        this.likes = likes;
        this.createdAt = createdAt;
        this.comments = comments;
        this.userId = userId;
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

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<CommentDisplayDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDisplayDTO> comments) {
        this.comments = comments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getReaction() {
        return reaction;
    }

    public void setReaction(int reaction) {
        this.reaction = reaction;
    }
}
