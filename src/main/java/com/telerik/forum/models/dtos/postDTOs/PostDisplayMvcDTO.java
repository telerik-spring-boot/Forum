package com.telerik.forum.models.dtos.postDTOs;

import java.time.LocalDateTime;

public class PostDisplayMvcDTO {


    private LocalDateTime createdOn;

    public PostDisplayMvcDTO() {
    }

    public PostDisplayMvcDTO(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }
}
