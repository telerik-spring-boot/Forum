package com.telerik.forum.helpers;


import com.telerik.forum.models.dtos.adminDTOs.AdminDisplayDTO;
import com.telerik.forum.models.dtos.adminDTOs.AdminUpdateDTO;
import com.telerik.forum.models.dtos.commentDTOs.CommentDisplayDTO;
import com.telerik.forum.models.dtos.postDTOs.PostDisplayDTO;
import com.telerik.forum.models.dtos.userDTOs.*;
import com.telerik.forum.models.user.AdminDetails;
import com.telerik.forum.models.user.User;
import com.telerik.forum.services.admin.AdminService;
import com.telerik.forum.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    private final UserService userService;
    private final AdminService adminService;
    private final PostMapper postMapper;

    @Autowired
    public UserMapper(UserService userService, AdminService adminService,
                      PostMapper postMapper) {
        this.userService = userService;
        this.adminService = adminService;
        this.postMapper = postMapper;
    }

    public User dtoToUser(UserCreateDTO dto) {
        User user = new User();

        user.setUsername(dto.getUsername());
        user.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmailAddress(dto.getEmailAddress());

        return user;
    }

    public User dtoToUser(int id, UserUpdateDTO dto, User userRequest) {
        User user = userService.getById(id, userRequest);

        if (dto.getEmailAddress() != null) {
            user.setEmailAddress(dto.getEmailAddress());
        }

        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }

        if (dto.getPassword() != null) {
            user.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
        }

        return user;
    }

    public UserDisplayDTO userToUserDisplayDTO(User user) {
        UserDisplayDTO userDisplayDTO = new UserDisplayDTO();

        userDisplayDTO.setUsername(user.getUsername());
        userDisplayDTO.setFirstName(user.getFirstName());
        userDisplayDTO.setLastName(user.getLastName());
        userDisplayDTO.setBlocked(user.isBlocked());
        userDisplayDTO.setUserId(user.getId());

        return userDisplayDTO;
    }

    public UserDisplayMvcDTO userToUserDisplayMVCDTO(User user) {
        UserDisplayMvcDTO userDisplayDTO = new UserDisplayMvcDTO();

        userDisplayDTO.setUsername(user.getUsername());
        userDisplayDTO.setName(user.getFirstName() + " " + user.getLastName());
        userDisplayDTO.setBlocked(user.isBlocked());
        userDisplayDTO.setLastLogin(user.getLastLogin());
        userDisplayDTO.setId(user.getId());

        return userDisplayDTO;
    }




    public UserCommentsDisplayDTO userToUserCommentsDisplayDTO(User user) {
        UserCommentsDisplayDTO userCommentsDisplayDTO = new UserCommentsDisplayDTO();

        List<CommentDisplayDTO> comments = user.getComments().stream()
                .map(postMapper::commentToCommentDisplayDTO)
                .toList();

        userCommentsDisplayDTO.setComments(comments);
        userCommentsDisplayDTO.setFirstName(user.getFirstName());
        userCommentsDisplayDTO.setLastName(user.getLastName());

        return userCommentsDisplayDTO;
    }

    public UserPostsDisplayDTO userToUserPostsDisplayDTO(User user) {
        UserPostsDisplayDTO userPostsDisplayDTO = new UserPostsDisplayDTO();

        userPostsDisplayDTO.setFirstName(user.getFirstName());
        userPostsDisplayDTO.setLastName(user.getLastName());

        List<PostDisplayDTO> posts = user.getPosts().stream()
                .map(postMapper::postToPostDisplayDTO)
                .toList();
        userPostsDisplayDTO.setPosts(posts);

        return userPostsDisplayDTO;
    }

    public AdminDisplayDTO adminToAdminDisplayDTO(AdminDetails admin) {
        AdminDisplayDTO adminDisplayDTO = new AdminDisplayDTO();
        User userPartOfAdmin = admin.getUser();

        adminDisplayDTO.setUsername(userPartOfAdmin.getUsername());
        adminDisplayDTO.setFirstName(userPartOfAdmin.getFirstName());
        adminDisplayDTO.setLastName(userPartOfAdmin.getLastName());
        adminDisplayDTO.setBlocked(userPartOfAdmin.isBlocked());
        adminDisplayDTO.setPhoneNumber(admin.getPhoneNumber());

        return adminDisplayDTO;

    }

    public AdminDetails dtoToAdmin(int userId, AdminUpdateDTO adminDTO, User userRequest) {
        AdminDetails admin = adminService.getByUserId(userId, userRequest);

        admin.setPhoneNumber(adminDTO.getPhoneNumber());

        return admin;
    }

    public UserUpdateMvcDTO userToUserUpdateMvcDto(User user) {
        UserUpdateMvcDTO userUpdateMvcDTO = new UserUpdateMvcDTO();

        userUpdateMvcDTO.setId(user.getId());
        userUpdateMvcDTO.setFirstName(user.getFirstName());
        userUpdateMvcDTO.setLastName(user.getLastName());
        userUpdateMvcDTO.setEmailAddress(user.getEmailAddress());
        userUpdateMvcDTO.setUsername(user.getUsername());

        return userUpdateMvcDTO;
    }

    public User userUpdateMvcDtoToUser(int id, UserUpdateMvcDTO udto, User userRequest) {
        User user = userService.getById(id, userRequest);

        if (udto.getFirstName() != null) {
            user.setFirstName(udto.getFirstName());
        }

        if (udto.getLastName() != null) {
            user.setLastName(udto.getLastName());
        }

        if (udto.getEmailAddress() != null) {
            user.setEmailAddress(udto.getEmailAddress());
        }

        return user;
    }

    public AdminDetails userUpdateMvcDtoToAdmin(int id, UserUpdateMvcDTO udto, User userRequest) {
        AdminDetails admin = adminService.getByUserId(id, userRequest);

        if (udto.getFirstName() != null) {
            admin.getUser().setFirstName(udto.getFirstName());
        }

        if (udto.getLastName() != null) {
            admin.getUser().setLastName(udto.getLastName());
        }

        if (udto.getEmailAddress() != null) {
            admin.getUser().setEmailAddress(udto.getEmailAddress());
        }

        if (udto.getPhoneNumber() != null) {
            admin.setPhoneNumber(udto.getPhoneNumber());
        }

        return admin;
    }
}
