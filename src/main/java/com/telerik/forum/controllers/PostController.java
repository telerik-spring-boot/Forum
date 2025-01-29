package com.telerik.forum.controllers;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.InvalidSortParameterException;
import com.telerik.forum.exceptions.InvalidUserInputException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.helpers.PostMapper;
import com.telerik.forum.models.post.Comment;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.user.User;
import com.telerik.forum.models.dtos.commentDTOs.CommentCreateDTO;
import com.telerik.forum.models.dtos.postDTOs.PostCreateDTO;
import com.telerik.forum.models.dtos.postDTOs.PostDisplayDTO;
import com.telerik.forum.models.dtos.tagDTOs.TagCreateAndDeleteDTO;
import com.telerik.forum.models.dtos.tagDTOs.TagUpdateDTO;
import com.telerik.forum.models.filters.FilterPostOptions;
import com.telerik.forum.services.comment.CommentService;
import com.telerik.forum.services.like.LikeService;
import com.telerik.forum.services.post.PostService;
import com.telerik.forum.services.tag.TagService;
import jakarta.validation.Valid;
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
    private final TagService tagService;

    @Autowired
    public PostController(PostService postService, AuthenticationHelper authenticationHelper,
                          PostMapper postMapper, CommentService commentService,
                          LikeService likeService, TagService tagService) {
        this.postService = postService;
        this.authenticationHelper = authenticationHelper;
        this.postMapper = postMapper;
        this.commentService = commentService;
        this.likeService = likeService;
        this.tagService = tagService;
    }

    @GetMapping
    public List<PostDisplayDTO> getAllPosts(@RequestHeader HttpHeaders headers,
                                            @RequestParam(required = false) String username,
                                            @RequestParam(required = false) String title,
                                            @RequestParam(required = false) String content,
                                            @RequestParam(required = false) String tags,
                                            @RequestParam(required = false) Long minLikes,
                                            @RequestParam(required = false) Long maxLikes,
                                            @RequestParam(required = false) String sortBy,
                                            @RequestParam(required = false) String sortOrder) {
        try {
            authenticationHelper.tryGetUser(headers);

            String[] tagArray = null;

            if (tags != null) {
                tagArray = tags.split(",");
            }

            FilterPostOptions options = new FilterPostOptions(username, title, content, tagArray,
                    minLikes, maxLikes, sortBy, sortOrder);

            return postService.getAllPostsWithFilters(options).stream()
                    .map(postMapper::postToPostDisplayDTO)
                    .toList();

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (InvalidSortParameterException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{postId}")
    public PostDisplayDTO getPostById(@RequestHeader HttpHeaders headers,
                                      @PathVariable int postId) {
        try {
            authenticationHelper.tryGetUser(headers);

            return postMapper.postToPostDisplayDTO(postService.getByIdWithCommentsAndLikesAndTags(postId));

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping
    public PostDisplayDTO createPost(@RequestHeader HttpHeaders headers,
                                     @Valid @RequestBody PostCreateDTO userInput) {
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
                                           @Valid @RequestBody CommentCreateDTO userInput) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);

            Comment comment = postMapper.dtoToComment(userInput);

            commentService.addComment(postId, comment, userRequest);

            return postMapper.postToPostDisplayDTO(postService.getByIdWithCommentsAndLikesAndTags(postId));

        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{postId}/tags")
    public PostDisplayDTO addTagsToPost(@RequestHeader HttpHeaders headers,
                                        @PathVariable int postId,
                                        @Valid @RequestBody TagCreateAndDeleteDTO userInput) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);

            tagService.addTagToPost(postId, userInput.getTags(), userRequest);

            return postMapper.postToPostDisplayDTO(postService.getByIdWithCommentsAndLikesAndTags(postId));

        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{postId}")
    public PostDisplayDTO updatePost(@RequestHeader HttpHeaders headers,
                                     @PathVariable int postId,
                                     @Valid @RequestBody PostCreateDTO userInput) {
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

            return postMapper.postToPostDisplayDTO(postService.getByIdWithCommentsAndLikesAndTags(postId));

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

            return postMapper.postToPostDisplayDTO(postService.getByIdWithCommentsAndLikesAndTags(postId));

        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{postId}/tags")
    public PostDisplayDTO UpdateTagsToPost(@RequestHeader HttpHeaders headers,
                                           @PathVariable int postId,
                                           @Valid @RequestBody TagUpdateDTO userInput) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);

            tagService.updateTagFromPost(postId, userInput.getOldTags(), userInput.getNewTags(), userRequest);

            return postMapper.postToPostDisplayDTO(postService.getByIdWithCommentsAndLikesAndTags(postId));

        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (InvalidUserInputException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public PostDisplayDTO updateComment(@RequestHeader HttpHeaders headers,
                                        @PathVariable int postId,
                                        @PathVariable int commentId,
                                        @Valid @RequestBody CommentCreateDTO userInput) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);

            Comment comment = postMapper.dtoToComment(commentId, userInput, postId);

            commentService.updateComment(comment, userRequest);

            return postMapper.postToPostDisplayDTO(postService.getByIdWithCommentsAndLikesAndTags(postId));

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

    @DeleteMapping("/{postId}/tags")
    public PostDisplayDTO removeTagsFromPost(@RequestHeader HttpHeaders headers,
                                             @PathVariable int postId,
                                             @Valid @RequestBody TagCreateAndDeleteDTO userInput) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);

            tagService.deleteTagFromPost(postId, userInput.getTags(), userRequest);

            return postMapper.postToPostDisplayDTO(postService.getByIdWithCommentsAndLikesAndTags(postId));

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

            return postMapper.postToPostDisplayDTO(postService.getByIdWithCommentsAndLikesAndTags(postId));

        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
