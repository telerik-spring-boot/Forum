package com.telerik.forum.models.dtos.userDTOs;

import com.telerik.forum.models.post.Comment;

import java.util.List;

public class UserCommentsPageDisplayDTO {

    private int id;

    private String username;

    private List<Comment> comments;


    public UserCommentsPageDisplayDTO() {
    }

    public UserCommentsPageDisplayDTO(int id, List<Comment> comments, String username) {
        this.id = id;
        this.comments = comments;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
