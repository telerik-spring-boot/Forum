package com.telerik.forum.models;

import com.telerik.forum.models.dtos.postDTOs.PostDisplayDTO;

import java.util.Set;

public class Home {

    private String coreFeatureURL;

    private int usersCount;

    private int postsCount;

    private  Set<PostDisplayDTO>  mostCommentedPosts;

    private  Set<PostDisplayDTO>  mostLikedPosts;

    public Home() {}

    public Home(String coreFeatureURL, int usersCount, int postsCount, Set<PostDisplayDTO> mostCommentedPosts, Set<PostDisplayDTO> mostLikedPosts) {
        this.coreFeatureURL = coreFeatureURL;
        this.usersCount = usersCount;
        this.postsCount = postsCount;
        this.mostCommentedPosts = mostCommentedPosts;
        this.mostLikedPosts = mostLikedPosts;
    }

    public String getCoreFeatureURL() {
        return coreFeatureURL;
    }

    public void setCoreFeatureURL(String coreFeatureURL) {
        this.coreFeatureURL = coreFeatureURL;
    }

    public Set<PostDisplayDTO> getMostLikedPosts() {
        return mostLikedPosts;
    }

    public void setMostLikedPosts(Set<PostDisplayDTO> mostLikedPosts) {
        this.mostLikedPosts = mostLikedPosts;
    }

    public Set<PostDisplayDTO> getMostCommentedPosts() {
        return mostCommentedPosts;
    }

    public void setMostCommentedPosts(Set<PostDisplayDTO> mostCommentedPosts) {
        this.mostCommentedPosts = mostCommentedPosts;
    }

    public int getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(int postsCount) {
        this.postsCount = postsCount;
    }

    public int getUsersCount() {
        return usersCount;
    }

    public void setUsersCount(int usersCount) {
        this.usersCount = usersCount;
    }
}
