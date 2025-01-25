package com.telerik.forum.models.filters;

import java.util.Optional;

public class FilterUserOptions {

    private final String username;
    private final String email;
    private final String firstName;
    private final String sortBy;
    private final String sortOrder;

    public FilterUserOptions(String username, String email, String firstName, String sortBy, String sortOrder) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    public Optional<String> getUsername() {
        return Optional.ofNullable(username);
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    public Optional<String> getFirstName() {
        return Optional.ofNullable(firstName);
    }

    public Optional<String> getSortBy() {
        return Optional.ofNullable(sortBy);
    }

    public Optional<String> getSortOrder() {
        return Optional.ofNullable(sortOrder);
    }


}
