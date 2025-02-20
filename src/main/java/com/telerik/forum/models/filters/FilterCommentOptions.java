package com.telerik.forum.models.filters;

import java.util.Optional;

public class FilterCommentOptions implements Sortable {

    private final String creatorUsername;

    private final String content;

    private final String sortBy;

    private final String sortOrder;

    public FilterCommentOptions(String creatorUsername, String content, String sortBy, String sortOrder) {
        this.creatorUsername = creatorUsername == null || creatorUsername.isBlank() ? null : creatorUsername;
        this.content = content == null || content.isBlank() ? null : content;
        this.sortBy = sortBy == null || sortBy.isBlank() ? null : sortBy;
        this.sortOrder = sortOrder == null || sortOrder.isBlank() ? null : sortOrder;
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
