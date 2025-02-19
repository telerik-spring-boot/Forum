package com.telerik.forum.models.dtos.tagDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TagCreateAndDeleteDTO {

    @NotBlank
    @Size(min = 1, max = 200, message = "Post tags must be between 1 and 200 symbols.")
    @Pattern(regexp = "^\\w+(,\\w+)*$", message = "The input must be a single word or words separated by commas without spaces.")
    private String tags;

    public TagCreateAndDeleteDTO() {
    }

    public TagCreateAndDeleteDTO(String tags) {
        this.tags = tags;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
