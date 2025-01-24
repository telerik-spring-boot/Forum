package com.telerik.forum.models.dtos.adminDTOs;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public class AdminCreateDTO {

    @Positive(message = "User ID must be a positive number")
    private int user_id;

    @Pattern(regexp = "^(\\+\\d{1,3}[-\\s]?)?\\(?\\d{1,4}\\)?[-\\s]?\\d{0,4}[-\\s]?\\d{1,4}[-\\s]?\\d{1,4}$", message = "Invalid phone number format.")
    private String phone_number;

    public AdminCreateDTO() {}

    public AdminCreateDTO(int user_id, String phone_number) {
        this.user_id = user_id;
        this.phone_number = phone_number;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
