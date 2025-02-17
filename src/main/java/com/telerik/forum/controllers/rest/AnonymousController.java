package com.telerik.forum.controllers.rest;


import com.telerik.forum.helpers.LoginHelper;
import com.telerik.forum.helpers.PostMapper;
import com.telerik.forum.models.Home;
import com.telerik.forum.models.dtos.userDTOs.UserLoginDTO;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.services.admin.AdminService;
import com.telerik.forum.services.post.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AnonymousController {


    private static final String CORE_FEATURE_URL = "http://localhost:8080/swagger-ui/index.html";
    private final AdminService adminService;
    private final PostService postService;
    private final PostMapper postMapper;
    private final LoginHelper loginHelper;

    @Autowired
    public AnonymousController(AdminService adminService, PostService postService, PostMapper postMapper, LoginHelper loginHelper) {
        this.adminService = adminService;
        this.postService = postService;
        this.postMapper = postMapper;
        this.loginHelper = loginHelper;
    }

    @GetMapping("/home")
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
        home.setUsersCount(adminService.getAllUsers().size());
        List<Post> posts = postService.getAllPosts();
        home.setPostsCount(posts.isEmpty() ? 0 : posts.size());

        return home;
    }

    @PostMapping("/login")
    public String loginUser(@Valid @RequestBody UserLoginDTO userInput) {

        return loginHelper.login(userInput);

    }
}
