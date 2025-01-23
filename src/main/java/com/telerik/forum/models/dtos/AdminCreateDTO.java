package com.telerik.forum.models.dtos;

import jakarta.validation.constraints.NotNull;

public class AdminCreateDTO {

    @NotNull
    private int user_id;

    @NotNull
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
