package com.telerik.forum.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PostCreateDTO {

    @NotBlank
    @Size(min = 16, max = 64, message = "Post title must be between 16 and 64 symbols.")
    private String title;

    @NotBlank
    @Size(min = 32, max = 8192, message = "Post must be between 32 and 8192 symbols.")
    private String content;

    public PostCreateDTO() {
    }

    public PostCreateDTO(String content, String title) {
        this.content = content;
        this.title = title;
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
}
