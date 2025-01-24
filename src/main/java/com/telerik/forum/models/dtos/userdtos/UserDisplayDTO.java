package com.telerik.forum.models.dtos.userdtos;

public class UserDisplayDTO {

    private String firstName;

    private String lastName;

    private String username;

    private boolean blocked;

    public UserDisplayDTO() {}

    public UserDisplayDTO(String firstName, String lastName, String username, boolean blocked) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.blocked = blocked;
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
