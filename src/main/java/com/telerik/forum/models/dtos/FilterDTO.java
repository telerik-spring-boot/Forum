package com.telerik.forum.models.dtos;

public class FilterDTO {

    private String creatorUsername;

    private String content;

    private String title;

    private String tags;

    private Long minLikes;

    private Long maxLikes;

    private String firstName;

    private String lastName;

    private String username;

    private String emailAddress;

    private String sortBy;

    private String sortOrder;


    public FilterDTO() {

    }

    public FilterDTO(String content, String sortBy, String sortOrder) {
        this.content = content;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    public FilterDTO(String creatorUsername, String content, String title, String tags, Long minLikes, Long maxLikes, String sortBy, String sortOrder) {
        this.creatorUsername = creatorUsername;
        this.content = content;
        this.title = title;
        this.tags = tags;
        this.minLikes = minLikes;
        this.maxLikes = maxLikes;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Long getMaxLikes() {
        return maxLikes;
    }

    public void setMaxLikes(Long maxLikes) {
        this.maxLikes = maxLikes;
    }

    public Long getMinLikes() {
        return minLikes;
    }

    public void setMinLikes(Long minLikes) {
        this.minLikes = minLikes;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
