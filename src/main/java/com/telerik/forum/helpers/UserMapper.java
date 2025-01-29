package com.telerik.forum.helpers;


import com.telerik.forum.models.user.AdminDetails;
import com.telerik.forum.models.user.User;
import com.telerik.forum.models.dtos.adminDTOs.AdminDisplayDTO;
import com.telerik.forum.models.dtos.adminDTOs.AdminUpdateDTO;
import com.telerik.forum.models.dtos.commentDTOs.CommentDisplayDTO;
import com.telerik.forum.models.dtos.postDTOs.PostDisplayDTO;
import com.telerik.forum.models.dtos.userDTOs.*;
import com.telerik.forum.services.admin.AdminService;
import com.telerik.forum.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    private final UserService userService;
    private final AdminService adminService;
    private final PostMapper postMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserMapper(UserService userService, AdminService adminService,
                      PostMapper postMapper, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.adminService = adminService;
        this.postMapper = postMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public User dtoToUser(UserCreateDTO dto) {
        User user = new User();

        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmailAddress(dto.getEmailAddress());

        return user;
    }

    public User dtoToUser(int id, UserUpdateDTO dto) {
        User user = userService.getById(id);

        if(dto.getEmailAddress() != null) {
            user.setEmailAddress(dto.getEmailAddress());
        }

        if(dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }

        if(dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }

        if(dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

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

    public AdminDisplayDTO AdminToAdminDisplayDTO(AdminDetails admin) {
        AdminDisplayDTO adminDisplayDTO = new AdminDisplayDTO();
        User userPartOfAdmin = admin.getUser();

        adminDisplayDTO.setUsername(userPartOfAdmin.getUsername());
        adminDisplayDTO.setFirstName(userPartOfAdmin.getFirstName());
        adminDisplayDTO.setLastName(userPartOfAdmin.getLastName());
        adminDisplayDTO.setBlocked(userPartOfAdmin.isBlocked());
        adminDisplayDTO.setPhoneNumber(admin.getPhoneNumber());

        return adminDisplayDTO;

    }

    public AdminDetails dtoToAdmin(int userId, AdminUpdateDTO adminDTO) {
        AdminDetails admin = adminService.getByUserId(userId);

        admin.setPhoneNumber(adminDTO.getPhoneNumber());

        return admin;
    }
}
