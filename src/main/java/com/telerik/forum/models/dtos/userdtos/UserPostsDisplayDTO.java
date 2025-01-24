package com.telerik.forum.models.dtos.userdtos;

import com.telerik.forum.models.Post;

import java.util.List;

public class UserPostsDisplayDTO {

    public String firstName;

    public String lastName;

    public List<Post> posts;

    public UserPostsDisplayDTO() {
    }

    public UserPostsDisplayDTO(String firstName, String lastName, List<Post> posts) {
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

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
