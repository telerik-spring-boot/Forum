package com.telerik.forum.models.dtos.userDTOs;

import java.time.LocalDateTime;

public class UserDisplayMvcDTO {

    private String name;

    private String username;

    private Long postCount;

    private Long commentCount;

    private LocalDateTime lastLogin;

    public UserDisplayMvcDTO() {
    }

    public UserDisplayMvcDTO(String name, String username, Long postCount, Long commentCount, LocalDateTime lastLogin) {
        this.name = name;
        this.username = username;
        this.postCount = postCount;
        this.commentCount = commentCount;
        this.lastLogin = lastLogin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    public Long getPostCount() {
        return postCount;
    }

    public void setPostCount(Long postCount) {
        this.postCount = postCount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
