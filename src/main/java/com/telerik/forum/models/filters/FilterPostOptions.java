package com.telerik.forum.models.filters;

import java.util.Optional;

public class FilterPostOptions implements Sortable{

    private final String creatorUsername;

    private final String title;

    private final String content;

    private final String [] tags;

    private final Integer likes;

    private final String sortBy;

    private final String sortOrder;

    public FilterPostOptions(String creatorUsername, String title, String content, String [] tags, Integer likes, String sortBy, String sortOrder) {
        this.creatorUsername = creatorUsername;
        this.title = title;
        this.content = content;
        this.tags = tags;
        this.likes = likes;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    public Optional<String> getCreatorUsername() {
        return Optional.ofNullable(creatorUsername);
    }

    public Optional<String> getTitle() {
        return Optional.ofNullable(title);
    }

    public Optional<String[]> getTags() {
        return Optional.ofNullable(tags);
    }

    public Optional<String> getContent() {
        return Optional.ofNullable(content);
    }

    public Optional<Integer> getLikes() {
        return Optional.ofNullable(likes);
    }

    public Optional<String> getSortBy() {
        return Optional.ofNullable(sortBy);
    }

    public Optional<String> getSortOrder() {
        return Optional.ofNullable(sortOrder);
    }
}
