package com.telerik.forum.models.filters;

import java.util.Optional;

public class FilterCommentOptions implements Sortable{

    private final String creatorUsername;

    private final String commentContent;

    private final String sortBy;

    private final String sortOrder;

    public FilterCommentOptions(String creatorUsername, String commentContent, String sortBy, String sortOrder) {
        this.creatorUsername = creatorUsername;
        this.commentContent = commentContent;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    public Optional<String> getCreatorUsername() {
        return Optional.ofNullable(creatorUsername);
    }

    public Optional<String> getCommentContent() {
        return Optional.ofNullable(commentContent);
    }

    public Optional<String> getSortBy() {
        return Optional.ofNullable(sortBy);
    }

    public Optional<String> getSortOrder() {
        return Optional.ofNullable(sortOrder);
    }
}
