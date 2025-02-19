package com.telerik.forum.models.dtos.userDTOs;

import com.telerik.forum.models.dtos.postDTOs.PostDisplayDTO;
import org.springframework.data.domain.Page;

public class UserPostsPageDisplayDTO {

    public String username;

    public int userId;

    public Page<PostDisplayDTO> posts;

    public UserPostsPageDisplayDTO() {
    }

    public UserPostsPageDisplayDTO(String username, int userId, Page<PostDisplayDTO> posts) {
        this.username = username;
        this.userId = userId;
        this.posts = posts;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Page<PostDisplayDTO> getPosts() {
        return posts;
    }

    public void setPosts(Page<PostDisplayDTO> posts) {
        this.posts = posts;
    }
}
