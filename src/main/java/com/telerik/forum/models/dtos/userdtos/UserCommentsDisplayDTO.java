package com.telerik.forum.models.dtos.userDTOs;

import com.telerik.forum.models.Comment;

import java.util.List;

public class UserCommentsDisplayDTO {

    public String firstName;

    public String lastName;

    public List<Comment> comments;

    public UserCommentsDisplayDTO() {
    }

    public UserCommentsDisplayDTO(String firstName, String lastName, List<Comment> comments) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.comments = comments;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
