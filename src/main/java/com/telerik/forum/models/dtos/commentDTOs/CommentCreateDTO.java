package com.telerik.forum.models.dtos.commentDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentCreateDTO {

    @NotBlank
    @Size(min = 1, max = 200, message = "Comment must be between 1 and 200 symbols.")
    private String content;

    public CommentCreateDTO() {
    }

    public CommentCreateDTO(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
