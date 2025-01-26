package com.telerik.forum.models.filters;

import java.util.Optional;

public class FilterCommentOptions implements Sortable{

    private final String creatorUsername;

    private final String content;

    private final String sortBy;

    private final String sortOrder;

    public FilterCommentOptions(String creatorUsername, String content, String sortBy, String sortOrder) {
        this.creatorUsername = creatorUsername;
        this.content = content;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    public Optional<String> getCreatorUsername() {
        return Optional.ofNullable(creatorUsername);
    }

    public Optional<String> getContent() {
        return Optional.ofNullable(content);
    }

    public Optional<String> getSortBy() {
        return Optional.ofNullable(sortBy);
    }

    public Optional<String> getSortOrder() {
        return Optional.ofNullable(sortOrder);
    }
}
