package com.telerik.forum.controllers;


import com.telerik.forum.exceptions.DuplicateEntityException;
import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.helpers.UserMapper;
import com.telerik.forum.models.User;
import com.telerik.forum.models.dtos.userDTOs.*;
import com.telerik.forum.models.filters.FilterCommentOptions;
import com.telerik.forum.models.filters.FilterPostOptions;
import com.telerik.forum.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, AuthenticationHelper authenticationHelper, UserMapper userMapper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.userMapper = userMapper;
    }



    @GetMapping("/{id}")
    public UserDisplayDTO getUserById(@PathVariable int id) {
        try {
            return userMapper.userToUserDisplayDTO(userService.getById(id));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}/posts")
    public UserPostsDisplayDTO getUserPosts(@RequestHeader HttpHeaders headers,
                                            @RequestParam(required = false) String creatorUsername,
                                            @RequestParam(required = false) String title,
                                            @RequestParam(required = false) String content,
                                            @RequestParam(required = false) String tags,
                                            @RequestParam(required = false) Integer likes,
                                            @RequestParam(required = false) String sortBy,
                                            @RequestParam(required = false) String sortOrder,
                                            @PathVariable int id) {
        try {
            authenticationHelper.tryGetUser(headers);
            FilterPostOptions options = new FilterPostOptions(creatorUsername,title,content,tags.split(","),likes, sortBy, sortOrder);
            User userEntity =  userService.getByIdWithPosts(id, options);

            return userMapper.userToUserPostsDisplayDTO(userEntity);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/{id}/comments")
    public UserCommentsDisplayDTO getUserComments(@RequestHeader HttpHeaders headers,
                                                  @RequestParam(required = false) String creatorUsername,
                                                  @RequestParam(required = false) String content,
                                                  @RequestParam(required = false) String sortBy,
                                                  @RequestParam(required = false) String sortOrder,
                                                  @PathVariable int id) {
        try {
            authenticationHelper.tryGetUser(headers);

            User userEntity = userService.getByIdWithComments(id, new FilterCommentOptions(creatorUsername, content, sortBy, sortOrder));

            return userMapper.userToUserCommentsDisplayDTO(userEntity);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping
    public UserDisplayDTO createUser(@Valid @RequestBody UserCreateDTO userInput) {
        try {

            User user = userMapper.dtoToUser(userInput);

            userService.create(user);

            return userMapper.userToUserDisplayDTO(user);

        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public UserDisplayDTO updateUser(@RequestHeader HttpHeaders headers, @PathVariable int id, @Valid @RequestBody UserUpdateDTO userInput) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);

            User userToBeUpdated = userMapper.dtoToUser(id, userInput);

            userService.update(userToBeUpdated, userRequest.getId());

            return userMapper.userToUserDisplayDTO(userService.getById(id));

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);

            userService.delete(id, userRequest.getId());

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }


}
