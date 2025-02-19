package com.telerik.forum.models.dtos.postDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PostUpdateDTO {

    @NotBlank
    @Size(min = 16, max = 64, message = "Post title must be between 16 and 64 symbols.")
    private String title;

    @NotBlank
    @Size(min = 32, max = 8192, message = "Post must be between 32 and 8192 symbols.")
    private String content;

    @Size( max = 200, message = "Post tags must be between 1 and 200 symbols.")
    @Pattern(regexp = "^(|\\w+(,\\w+)*)$", message = "The input must be a single word or words separated by commas without spaces.")
    private String tags;

    public PostUpdateDTO() {
    }

    public PostUpdateDTO(String content, String title, String tags) {
        this.content = content;
        this.title = title;
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
