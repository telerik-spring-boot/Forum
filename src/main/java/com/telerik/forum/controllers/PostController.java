package com.telerik.forum.controllers;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.helpers.PostMapper;
import com.telerik.forum.models.Post;
import com.telerik.forum.models.User;
import com.telerik.forum.models.dtos.postDTOs.PostCreateDTO;
import com.telerik.forum.models.dtos.postDTOs.PostDisplayDTO;
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
    private final PostMapper postMapper;

    @Autowired
    public PostController(PostService postService, AuthenticationHelper authenticationHelper, PostMapper postMapper) {
        this.postService = postService;
        this.authenticationHelper = authenticationHelper;
        this.postMapper = postMapper;
    }

    @GetMapping
    public List<PostDisplayDTO> getAllPosts() {
        return postService.getPosts().stream()
                .map(postMapper::postToPostDisplayDTO)
                .toList();
    }

    @GetMapping("/{postId}")
    public PostDisplayDTO getPostById(@PathVariable int postId) {
        try {
            return postMapper.postToPostDisplayDTO(postService.getPost(postId));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    public PostDisplayDTO createPost(@RequestHeader HttpHeaders headers,
                           @RequestBody PostCreateDTO userInput) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);
            Post post = postMapper.dtoToPost(userInput);
            postService.createPost(post, userRequest);
            return postMapper.postToPostDisplayDTO(post);

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
