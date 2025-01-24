package com.telerik.forum.controllers;


import com.telerik.forum.exceptions.DuplicateEntityException;
import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.models.Comment;
import com.telerik.forum.models.Post;
import com.telerik.forum.models.User;
import com.telerik.forum.services.UserService;
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

    @Autowired
    public UserController(UserService userService, AuthenticationHelper authenticationHelper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
    }


    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        try {
            return userService.getById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}/posts")
    public List<Post> getUserPosts(@PathVariable int id) {
        try {
            User userEntity =  userService.getByIdWithPosts(id);

            return userEntity.getPosts();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}/comments")
    public List<Comment> getUserComments(@PathVariable int id) {
        try {
            User userEntity =  userService.getByIdWithComments(id);

            return userEntity.getComments();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    public User createUser(@RequestHeader HttpHeaders headers, @RequestBody User userInput) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);

            userService.create(userInput, userRequest);

            return userInput;

        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public User updateUser(@RequestHeader HttpHeaders headers, @PathVariable int id, @RequestBody User userInput) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);

            userService.update(userInput, userRequest);

            return userService.getById(id);

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

            userService.delete(id, userRequest);

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }


}
