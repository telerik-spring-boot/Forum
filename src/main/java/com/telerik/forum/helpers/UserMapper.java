package com.telerik.forum.helpers;


import com.telerik.forum.models.Post;
import com.telerik.forum.models.User;
import com.telerik.forum.models.dtos.userdtos.UserCommentsDisplayDTO;
import com.telerik.forum.models.dtos.userdtos.UserCreateDTO;
import com.telerik.forum.models.dtos.userdtos.UserDisplayDTO;
import com.telerik.forum.models.dtos.userdtos.UserPostsDisplayDTO;
import com.telerik.forum.services.UserService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    private final UserService userService;

    public UserMapper(UserService userService) {
        this.userService = userService;
    }

    public User dtoToUser(UserCreateDTO dto) {
        User user = new User();

        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmailAddress(dto.getEmailAddress());

        return user;
    }

    public UserDisplayDTO userToUserDisplayDTO(User user) {
        UserDisplayDTO userDisplayDTO = new UserDisplayDTO();

        userDisplayDTO.setUsername(user.getUsername());
        userDisplayDTO.setFirstName(user.getFirstName());
        userDisplayDTO.setLastName(user.getLastName());
        userDisplayDTO.setBlocked(user.isBlocked());

        return userDisplayDTO;
    }

    public UserCommentsDisplayDTO userToUserCommentsDisplayDTO(User user) {
        UserCommentsDisplayDTO userCommentsDisplayDTO = new UserCommentsDisplayDTO();

        userCommentsDisplayDTO.setComments(user.getComments());
        userCommentsDisplayDTO.setFirstName(user.getFirstName());
        userCommentsDisplayDTO.setLastName(user.getLastName());

        return userCommentsDisplayDTO;
    }

    public UserPostsDisplayDTO userToUserPostsDisplayDTO(User user) {
        UserPostsDisplayDTO userPostsDisplayDTO = new UserPostsDisplayDTO();

        userPostsDisplayDTO.setFirstName(user.getFirstName());
        userPostsDisplayDTO.setLastName(user.getLastName());
        userPostsDisplayDTO.setPosts(user.getPosts());

        return userPostsDisplayDTO;
    }

}
