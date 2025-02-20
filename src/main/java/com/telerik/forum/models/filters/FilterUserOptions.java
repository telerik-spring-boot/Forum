package com.telerik.forum.models.filters;

import java.util.Optional;

public class FilterUserOptions implements Sortable {

    private final String username;
    private final String emailAddress;
    private final String firstName;
    private final String sortBy;
    private final String sortOrder;

    public FilterUserOptions(String username, String emailAddress, String firstName, String sortBy, String sortOrder) {
        this.username = username == null || username.isBlank() ? null : username;
        this.emailAddress = emailAddress == null || emailAddress.isBlank() ? null : emailAddress;
        this.firstName = firstName == null || firstName.isBlank() ? null : firstName;
        this.sortBy = sortBy == null || sortBy.isBlank() ? null : sortBy;
        this.sortOrder = sortOrder == null || sortOrder.isBlank() ? null : sortOrder;
    }

    public Optional<String> getUsername() {
        return Optional.ofNullable(username);
    }

    public Optional<String> getEmailAddress() {
        return Optional.ofNullable(emailAddress);
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
