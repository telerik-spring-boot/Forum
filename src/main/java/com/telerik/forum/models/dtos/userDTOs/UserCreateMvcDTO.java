package com.telerik.forum.models.dtos.userDTOs;

import jakarta.validation.constraints.NotBlank;

public class UserCreateMvcDTO extends UserCreateDTO {

    @NotBlank(message = "Confirm password is required.")
    private String confirmPassword;

    public UserCreateMvcDTO() {
    }

    public UserCreateMvcDTO(String firstName, String lastName, String email, String username, String password, String confirmPassword) {
        super(firstName, lastName, email, username, password);
        this.confirmPassword = confirmPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
