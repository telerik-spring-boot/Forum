package com.telerik.forum.controllers;


import com.telerik.forum.exceptions.DuplicateEntityException;
import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.helpers.UserMapper;
import com.telerik.forum.models.User;
import com.telerik.forum.models.dtos.userDTOs.UserCommentsDisplayDTO;
import com.telerik.forum.models.dtos.userDTOs.UserCreateDTO;
import com.telerik.forum.models.dtos.userDTOs.UserDisplayDTO;
import com.telerik.forum.models.dtos.userDTOs.UserPostsDisplayDTO;
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


    @GetMapping
    public List<UserDisplayDTO> getAllUsers() {
        return userService.getAll().stream()
                .map(userMapper::userToUserDisplayDTO)
                .toList();
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
    public UserPostsDisplayDTO getUserPosts(@PathVariable int id) {
        try {
            User userEntity =  userService.getByIdWithPosts(id);

            return userMapper.userToUserPostsDisplayDTO(userEntity);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}/comments")
    public UserCommentsDisplayDTO getUserComments(@PathVariable int id) {
        try {
            User userEntity = userService.getByIdWithComments(id);

            return userMapper.userToUserCommentsDisplayDTO(userEntity);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    public UserDisplayDTO createUser(@RequestHeader HttpHeaders headers,@Valid @RequestBody UserCreateDTO userInput) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);

            User user = userMapper.dtoToUser(userInput);

            userService.create(user, userRequest);

            return userMapper.userToUserDisplayDTO(user);

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
