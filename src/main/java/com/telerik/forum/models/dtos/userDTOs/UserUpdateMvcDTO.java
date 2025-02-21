package com.telerik.forum.models.dtos.userDTOs;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserUpdateMvcDTO {

    @Size(min = 4, max = 32, message = "First name must be between 4 and 32 symbols.")
    private String firstName;

    @Size(min = 4, max = 32, message = "Last name must be between 4 and 32 symbols.")
    private String lastName;

    @Size(min = 5, max = 254, message = "Email must be between 5 and 254 symbols.")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Invalid email address")
    private String emailAddress;

    @Pattern(regexp = "^(\\+\\d{1,3}[-\\s]?)?\\(?\\d{1,4}\\)?[-\\s]?\\d{0,4}[-\\s]?\\d{1,4}[-\\s]?\\d{1,4}$", message = "Invalid phone number format.")
    private String phoneNumber;

    private String username;

    private Boolean isAdmin;

    public UserUpdateMvcDTO() {
    }

    public UserUpdateMvcDTO(String firstName, String lastName, String emailAddress, String username) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }
}
