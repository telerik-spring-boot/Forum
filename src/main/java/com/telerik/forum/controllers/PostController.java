package com.telerik.forum.controllers;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.helpers.PostMapper;
import com.telerik.forum.models.Comment;
import com.telerik.forum.models.Post;
import com.telerik.forum.models.User;
import com.telerik.forum.models.dtos.commentDTOs.CommentCreateDTO;
import com.telerik.forum.models.dtos.postDTOs.PostCreateDTO;
import com.telerik.forum.models.dtos.postDTOs.PostDisplayDTO;
import com.telerik.forum.services.CommentService;
import com.telerik.forum.services.LikeService;
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
    private final CommentService commentService;
    private final LikeService likeService;

    @Autowired
    public PostController(PostService postService, AuthenticationHelper authenticationHelper,
                          PostMapper postMapper, CommentService commentService,
                          LikeService likeService) {
        this.postService = postService;
        this.authenticationHelper = authenticationHelper;
        this.postMapper = postMapper;
        this.commentService = commentService;
        this.likeService = likeService;
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
            return postMapper.postToPostDisplayDTO(postService.getByIdWithCommentsAndLikes(postId));
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

    @PostMapping("/{postId}/comments")
    public PostDisplayDTO addCommentToPost(@RequestHeader HttpHeaders headers,
                                           @PathVariable int postId,
                                           @RequestBody CommentCreateDTO userInput) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);
            Comment comment = postMapper.dtoToComment(userInput);
            commentService.addComment(postId, comment, userRequest);
            return postMapper.postToPostDisplayDTO(postService.getByIdWithCommentsAndLikes(postId));
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{postId}")
    public PostDisplayDTO updatePost(@RequestHeader HttpHeaders headers,
                                     @PathVariable int postId,
                                     @RequestBody PostCreateDTO userInput) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);
            Post post = postMapper.dtoToPost(postId, userInput);
            postService.updatePost(post, userRequest);
            return postMapper.postToPostDisplayDTO(post);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{postId}/like")
    public PostDisplayDTO likePost(@RequestHeader HttpHeaders headers,
                                   @PathVariable int postId) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);
            likeService.likePost(postId, userRequest);
            Post post =postService.getByIdWithCommentsAndLikes(postId);
            return postMapper.postToPostDisplayDTO(post);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{postId}/dislike")
    public PostDisplayDTO dislikePost(@RequestHeader HttpHeaders headers,
                                      @PathVariable int postId) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);
            likeService.dislikePost(postId, userRequest);
            return postMapper.postToPostDisplayDTO(postService.getByIdWithCommentsAndLikes(postId));
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public PostDisplayDTO updateComment(@RequestHeader HttpHeaders headers,
                                        @PathVariable int postId,
                                        @PathVariable int commentId,
                                        @RequestBody CommentCreateDTO userInput) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);
            Comment comment = postMapper.dtoToComment(commentId, userInput, postId);
            commentService.updateComment(comment, userRequest);
            return postMapper.postToPostDisplayDTO(postService.getByIdWithCommentsAndLikes(postId));
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@RequestHeader HttpHeaders headers,
                           @PathVariable int postId) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);
            postService.deletePost(postId, userRequest);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public PostDisplayDTO deleteComment(@RequestHeader HttpHeaders headers,
                                        @PathVariable int postId,
                                        @PathVariable int commentId) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);
            commentService.deleteComment(postId, commentId, userRequest);
            return postMapper.postToPostDisplayDTO(postService.getByIdWithCommentsAndLikes(postId));
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
