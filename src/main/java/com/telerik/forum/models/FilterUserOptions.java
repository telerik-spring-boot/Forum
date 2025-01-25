package com.telerik.forum.models;

import java.util.Optional;

public class FilterUserOptions {

    private final String username;
    private final String email;
    private final String firstName;

    public FilterUserOptions(String username, String email, String firstName) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
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


}
