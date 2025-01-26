package com.telerik.forum.models.dtos.tagDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TagDTO {

    @NotBlank
    @Size(min = 4, max = 200, message= "Post tags must be between 4 and 200 symbols.")
    private String tags;

    public TagDTO() {
    }

    public TagDTO(String tags) {
        this.tags = tags;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
