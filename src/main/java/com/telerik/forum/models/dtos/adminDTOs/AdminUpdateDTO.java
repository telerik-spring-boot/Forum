package com.telerik.forum.models.dtos.adminDTOs;

import jakarta.validation.constraints.Pattern;

public class AdminUpdateDTO {

    @Pattern(regexp = "^(\\+\\d{1,3}[-\\s]?)?\\(?\\d{1,4}\\)?[-\\s]?\\d{0,4}[-\\s]?\\d{1,4}[-\\s]?\\d{1,4}$", message = "Invalid phone number format.")
    private String phoneNumber;

    public AdminUpdateDTO() {}

    public AdminUpdateDTO(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
