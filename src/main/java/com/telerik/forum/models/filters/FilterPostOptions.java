package com.telerik.forum.models.filters;

import java.util.Optional;

public class FilterPostOptions extends FilterCommentOptions {

    private final String creatorUsername;

    private final String title;

    private final String[] tags;

    private final Long minLikes;

    private final Long maxLikes;

    public FilterPostOptions(String creatorUsername, String title, String content, String[] tags, Long minLikes, Long maxLikes, String sortBy, String sortOrder) {
        super(creatorUsername, content, sortBy, sortOrder);
        this.creatorUsername = creatorUsername;
        this.title = title;
        this.tags = tags;
        this.minLikes = minLikes;
        this.maxLikes = maxLikes;
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

    public Optional<Long> getMinLikes() {
        return Optional.ofNullable(minLikes);
    }

    public Optional<Long> getMaxLikes() {
        return Optional.ofNullable(maxLikes);
    }
}
