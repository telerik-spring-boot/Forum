package com.telerik.forum.controllers;


import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.helpers.UserMapper;
import com.telerik.forum.models.dtos.userDTOs.*;
import com.telerik.forum.models.filters.FilterCommentOptions;
import com.telerik.forum.models.filters.FilterPostOptions;
import com.telerik.forum.models.user.User;
import com.telerik.forum.services.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, AuthenticationHelper authenticationHelper,
                          UserMapper userMapper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.userMapper = userMapper;
    }


    @GetMapping("/{id}")
    public UserDisplayDTO getUserById(@RequestHeader HttpHeaders headers, @PathVariable int id) {

        User userRequest = authenticationHelper.tryGetUser(headers);

        return userMapper.userToUserDisplayDTO(userService.getById(id, userRequest));

    }

    @GetMapping("/{id}/posts")
    public UserPostsDisplayDTO getUserPosts(@RequestHeader HttpHeaders headers,
                                            @RequestParam(required = false) String title,
                                            @RequestParam(required = false) String content,
                                            @RequestParam(required = false) String tags,
                                            @RequestParam(required = false) Long minLikes,
                                            @RequestParam(required = false) Long maxLikes,
                                            @RequestParam(required = false) String sortBy,
                                            @RequestParam(required = false) String sortOrder,
                                            @PathVariable int id) {

        User userRequest = authenticationHelper.tryGetUser(headers);
        String[] tagArray = null;

        if (tags != null) {
            tagArray = tags.split(",");
        }

        FilterPostOptions options = new FilterPostOptions(null, title, content, tagArray, minLikes, maxLikes, sortBy, sortOrder);

        User userEntity = userService.getByIdWithPosts(id, options, userRequest);

        return userMapper.userToUserPostsDisplayDTO(userEntity);

    }

    @GetMapping("/{id}/comments")
    public UserCommentsDisplayDTO getUserComments(@RequestHeader HttpHeaders headers,
                                                  @RequestParam(required = false) String commentContent,
                                                  @RequestParam(required = false) String sortBy,
                                                  @RequestParam(required = false) String sortOrder,
                                                  @PathVariable int id) {

        User userRequest = authenticationHelper.tryGetUser(headers);

        User userEntity = userService.getByIdWithComments(id, new FilterCommentOptions(null, commentContent, sortBy, sortOrder), userRequest);

        return userMapper.userToUserCommentsDisplayDTO(userEntity);

    }

    @PostMapping
    public UserDisplayDTO createUser(@Valid @RequestBody UserCreateDTO userInput) {

        User user = userMapper.dtoToUser(userInput);

        userService.create(user);

        return userMapper.userToUserDisplayDTO(user);

    }


    @PutMapping("/{id}")
    public UserDisplayDTO updateUser(@RequestHeader HttpHeaders headers, @PathVariable int id, @Valid @RequestBody UserUpdateDTO userInput) {

        User userRequest = authenticationHelper.tryGetUserWithRoles(headers);

        User userToBeUpdated = userMapper.dtoToUser(id, userInput, userRequest);

        userService.update(userToBeUpdated, userRequest);

        return userMapper.userToUserDisplayDTO(userToBeUpdated);

    }

    @DeleteMapping("/{id}")
    public void deleteUser(@RequestHeader HttpHeaders headers, @PathVariable int id) {

        User userRequest = authenticationHelper.tryGetUserWithRoles(headers);

        userService.delete(id, userRequest);

    }

}
