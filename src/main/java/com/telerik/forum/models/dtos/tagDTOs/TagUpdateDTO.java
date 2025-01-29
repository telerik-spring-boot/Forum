package com.telerik.forum.models.dtos.tagDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TagUpdateDTO {
    @NotBlank
    @Size(min = 1, max = 200, message = "Post tags must be between 4 and 200 symbols.")
    @Pattern(regexp = "^\\w+(,\\w+)*$", message = "The input must be a single word or words separated by commas without spaces.")
    private String oldTags;

    @NotBlank
    @Size(min = 1, max = 200, message = "Post tags must be between 4 and 200 symbols.")
    @Pattern(regexp = "^\\w+(,\\w+)*$", message = "The input must be a single word or words separated by commas without spaces.")
    private String newTags;

    public TagUpdateDTO() {
    }

    public TagUpdateDTO(String oldTags, String newTags) {
        this.oldTags = oldTags;
        this.newTags = newTags;
    }

    public String getOldTags() {
        return oldTags;
    }

    public void setOldTags(String oldTags) {
        this.oldTags = oldTags;
    }

    public String getNewTags() {
        return newTags;
    }

    public void setNewTags(String newTags) {
        this.newTags = newTags;
    }
}
