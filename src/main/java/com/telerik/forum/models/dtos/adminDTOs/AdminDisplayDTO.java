package com.telerik.forum.models.dtos.adminDTOs;

import com.telerik.forum.models.dtos.userDTOs.UserDisplayDTO;

public class AdminDisplayDTO extends UserDisplayDTO {

    private String phoneNumber;


    public AdminDisplayDTO() {}

    public AdminDisplayDTO(String firstName, String lastName, String username, boolean blocked, String phoneNumber) {
        super(firstName, lastName, username, blocked);
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
