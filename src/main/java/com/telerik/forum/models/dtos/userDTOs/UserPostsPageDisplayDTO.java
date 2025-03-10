package com.telerik.forum.models.dtos.userDTOs;

import com.telerik.forum.models.dtos.postDTOs.PostDisplayDTO;
import com.telerik.forum.models.post.Post;
import org.springframework.data.domain.Page;

public class UserPostsPageDisplayDTO {

    public String username;

    public int id;

    public Page<PostDisplayDTO> posts;

    public UserPostsPageDisplayDTO() {
    }

    public UserPostsPageDisplayDTO(String username, int id, Page<PostDisplayDTO> posts) {
        this.username = username;
        this.id = id;
        this.posts = posts;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Page<PostDisplayDTO> getPosts() {
        return posts;
    }

    public void setPosts(Page<PostDisplayDTO> posts) {
        this.posts = posts;
    }
}
