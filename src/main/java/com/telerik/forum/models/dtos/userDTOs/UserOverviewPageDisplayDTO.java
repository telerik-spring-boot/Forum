package com.telerik.forum.models.dtos.userDTOs;

import com.telerik.forum.models.dtos.PostCommentWrapper;

import java.util.List;

public class UserOverviewPageDisplayDTO {

    private int id;

    private String username;

    private List<PostCommentWrapper> entities;

    public UserOverviewPageDisplayDTO() {
    }

    public List<PostCommentWrapper> getEntities() {
        return entities;
    }

    public void setEntities(List<PostCommentWrapper> entities) {
        this.entities = entities;
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
}
