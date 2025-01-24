package com.telerik.forum.models.dtos.userDTOs;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserUpdateDTO {

    @Size(min = 4, max = 32, message = "First name must be between 4 and 32 symbols.")
    private String firstName;

    @Size(min = 4, max = 32, message = "Last name must be between 4 and 32 symbols.")
    private String lastName;

    @Size(min = 5, max = 254, message = "Email must be between 5 and 254 symbols.")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Invalid email address")
    private String emailAddress;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{6,20}$", message = "Password must contain at least one uppercase letter, one lowercase letter, one number, one symbol, and be between 6 to 20 characters long.")
    private String password;

    public UserUpdateDTO() {}

    public UserUpdateDTO(String firstName, String lastName, String emailAddress, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
