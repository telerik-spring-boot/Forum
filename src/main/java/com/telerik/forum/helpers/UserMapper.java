package com.telerik.forum.helpers;


import com.telerik.forum.models.User;
import com.telerik.forum.models.dtos.userdtos.UserCreateDTO;
import com.telerik.forum.models.dtos.userdtos.UserDisplayDTO;
import com.telerik.forum.services.UserService;
import org.springframework.stereotype.Component;

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

}
