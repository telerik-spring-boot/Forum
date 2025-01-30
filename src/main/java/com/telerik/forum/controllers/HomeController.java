package com.telerik.forum.controllers;


import com.telerik.forum.helpers.PostMapper;
import com.telerik.forum.models.Home;
import com.telerik.forum.models.filters.FilterUserOptions;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.user.User;
import com.telerik.forum.services.admin.AdminService;
import com.telerik.forum.services.post.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/home")
public class HomeController {


    private static final String CORE_FEATURE_URL = "http://localhost:8080/swagger-ui/index.html";
    private final AdminService adminService;
    private final PostService postService;
    private final PostMapper postMapper;

    @Autowired
    public HomeController(AdminService adminService, PostService postService, PostMapper postMapper) {
        this.adminService = adminService;
        this.postService = postService;
        this.postMapper = postMapper;
    }

    @GetMapping
    public Home homePage() {
        Home home = new Home();

        home.setCoreFeatureURL(CORE_FEATURE_URL);
        home.setMostCommentedPosts(postService.getMostCommentedPosts(10).stream()
                .map(postMapper::postToPostDisplayDTO)
                .collect(Collectors.toSet()));
        home.setMostLikedPosts(postService.getMostLikedPosts(10).stream()
                .map(postMapper::postToPostDisplayDTO)
                .collect(Collectors.toSet()));
        home.setMostRecentPosts(postService.getMostRecentPosts(10).stream()
                .map(postMapper::postToPostDisplayDTO)
                .collect(Collectors.toSet()));
        home.setUsersCount(adminService.getAllUsers(new FilterUserOptions(null, null, null, null, null), adminService.getAll(null).get(0).getUser()).size());
        List<Post> posts = postService.getAllPosts();
        home.setPostsCount(posts.isEmpty() ? 0 : posts.size());

        return home;
    }
}
