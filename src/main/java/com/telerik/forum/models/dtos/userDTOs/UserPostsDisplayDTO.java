package com.telerik.forum.models.dtos.userDTOs;

import com.telerik.forum.models.Post;
import com.telerik.forum.models.dtos.postDTOs.PostDisplayDTO;

import java.util.List;

public class UserPostsDisplayDTO {

    public String firstName;

    public String lastName;

    public List<PostDisplayDTO> posts;

    public UserPostsDisplayDTO() {
    }

    public UserPostsDisplayDTO(String firstName, String lastName, List<PostDisplayDTO> posts) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.posts = posts;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public List<PostDisplayDTO> getPosts() {
        return posts;
    }

    public void setPosts(List<PostDisplayDTO> posts) {
        this.posts = posts;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
