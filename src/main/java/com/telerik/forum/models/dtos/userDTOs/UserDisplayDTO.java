package com.telerik.forum.models.dtos.userDTOs;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDisplayDTO {

    private String firstName;

    private String lastName;

    private String username;

    private boolean blocked;

    @JsonIgnore
    private int userId;

    public UserDisplayDTO() {}

    public UserDisplayDTO(String firstName, String lastName, String username, boolean blocked) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.blocked = blocked;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
