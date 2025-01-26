package com.telerik.forum.models.filters;

import java.util.Optional;

public class FilterCommentOptions {

    private final String creatorUsername;

    private final String content;

    public FilterCommentOptions(String creatorUsername, String content) {
        this.creatorUsername = creatorUsername;
        this.content = content;
    }

    public Optional<String> getCreatorUsername() {
        return Optional.ofNullable(creatorUsername);
    }

    public Optional<String> getContent() {
        return Optional.ofNullable(content);
    }
}
