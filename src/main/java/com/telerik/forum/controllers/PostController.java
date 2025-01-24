package com.telerik.forum.controllers;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.models.Post;
import com.telerik.forum.models.User;
import com.telerik.forum.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/posts")
public class PostController {
    private final PostService postService;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public PostController(PostService postService, AuthenticationHelper authenticationHelper) {
        this.postService = postService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getPosts();
    }

    @GetMapping("/{postId}")
    public Post getPostById(@PathVariable int postId) {
        try {
            return postService.getPost(postId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    public Post createPost(@RequestHeader HttpHeaders headers,
                           @RequestBody Post post) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);
            postService.createPost(post, userRequest);
            return post;

        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{postId}")
    public Post updatePost(@RequestHeader HttpHeaders headers,
                           @PathVariable int postId,
                           @RequestBody Post post) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);
            postService.updatePost(post, userRequest);
            return post;
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@RequestHeader HttpHeaders headers,
                           @PathVariable int postId) {
        try{
            User userRequest = authenticationHelper.tryGetUser(headers);
            postService.deletePost(postId, userRequest);
        }catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
