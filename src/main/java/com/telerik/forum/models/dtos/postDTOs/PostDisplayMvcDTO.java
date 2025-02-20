package com.telerik.forum.models.dtos.postDTOs;

import com.telerik.forum.models.dtos.commentDTOs.CommentDisplayDTO;

import java.time.LocalDateTime;
import java.util.List;

public class PostDisplayMvcDTO extends PostDisplayDTO {

    private int id;

    public PostDisplayMvcDTO() {
    }

    public PostDisplayMvcDTO(String creatorUsername, String title, String content, List<String> tags, int likes, LocalDateTime createdAt, List<CommentDisplayDTO> comments, int id) {
        super(creatorUsername, title, content, tags, likes, createdAt, comments);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
