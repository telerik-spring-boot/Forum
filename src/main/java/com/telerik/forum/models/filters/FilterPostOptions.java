package com.telerik.forum.models.filters;

import java.util.List;
import java.util.Optional;

public class FilterPostOptions {

    private final String creatorUsername;

    private final String title;

    private final String content;

    private final String [] tags;

    private final Integer likes;

    public FilterPostOptions(String creatorUsername, String title, String content, String [] tags, Integer likes) {
        this.creatorUsername = creatorUsername;
        this.title = title;
        this.content = content;
        this.tags = tags;
        this.likes = likes;
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
}
