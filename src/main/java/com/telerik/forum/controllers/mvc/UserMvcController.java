package com.telerik.forum.controllers.mvc;


import com.telerik.forum.configurations.jwt.JwtUtil;
import com.telerik.forum.exceptions.DuplicateEntityException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.helpers.FilesHelper;
import com.telerik.forum.helpers.UserMapper;
import com.telerik.forum.models.dtos.FilterDTO;
import com.telerik.forum.models.dtos.commentDTOs.CommentCreateDTO;
import com.telerik.forum.models.dtos.userDTOs.*;
import com.telerik.forum.models.filters.FilterCommentOptions;
import com.telerik.forum.models.filters.FilterPostOptions;
import com.telerik.forum.models.user.AdminDetails;
import com.telerik.forum.models.user.User;
import com.telerik.forum.services.admin.AdminService;
import com.telerik.forum.services.user.UserService;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/users")
public class UserMvcController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final UserMapper userMapper;
    private final AdminService adminService;
    private final JwtUtil jwtUtil;

    public UserMvcController(UserService userService, AuthenticationHelper authenticationHelper, UserMapper userMapper, AdminService adminService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.userMapper = userMapper;
        this.adminService = adminService;
        this.jwtUtil = jwtUtil;
    }


    @GetMapping("/{id}/posts")
    public String showUserPosts(HttpSession session,
                                @ModelAttribute("filterOptions") FilterDTO filterDto,
                                @PathVariable int id,
                                @PageableDefault(size = 10, page = 0) Pageable pageable,
                                Model model,
                                HttpServletRequest request) {
        try {
            User user = authenticationHelper.tryGetUserMvc(session);

            String[] tagArray = null;

            if (filterDto.getTags() != null) {
                tagArray = filterDto.getTags().split(",");
            }


            FilterPostOptions options = new FilterPostOptions(null, filterDto.getTitle(), filterDto.getContent(), tagArray, filterDto.getMinLikes(), filterDto.getMaxLikes(), filterDto.getSortBy(), filterDto.getSortOrder());

            UserPostsPageDisplayDTO showUser = userService.getByIdWithPosts(id, options, user, pageable);

            model.addAttribute("user", showUser);
            model.addAttribute("userId", user.getId());
            model.addAttribute("comment", new CommentCreateDTO());
            model.addAttribute("currentURI", request.getRequestURI());

            return "user";
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }
    }


    @GetMapping("/{id}/overview")
    public String showUserOverview(HttpSession session,
                                   @ModelAttribute("filterOptions") FilterDTO filterDto,
                                   @PathVariable int id,
                                   @PageableDefault(size = 10, page = 0) Pageable pageable,
                                   Model model,
                                   HttpServletRequest request) {
        try {
            User user = authenticationHelper.tryGetUserMvc(session);

            FilterPostOptions postOptions = new FilterPostOptions(null, null, filterDto.getContent(), null, null, null, filterDto.getSortBy(), filterDto.getSortOrder());
            UserOverviewPageDisplayDTO showUser = userService.getByIdWithCommentsAndPosts(id, postOptions, postOptions, user, pageable);

            model.addAttribute("user", showUser);
            model.addAttribute("userId", user.getId());
            model.addAttribute("comment", new CommentCreateDTO());
            model.addAttribute("currentURI", request.getRequestURI());

            return "user";
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }
    }


    @GetMapping("/{id}/comments")
    public String getUserComments(HttpSession session,
                                  @ModelAttribute("filterOptions") FilterDTO filterDto,
                                  @PathVariable int id,
                                  Model model,
                                  HttpServletRequest request) {

        try {
            User user = authenticationHelper.tryGetUserMvc(session);


            FilterPostOptions commentOptions = new FilterPostOptions(null, null, filterDto.getContent(), null, null, null,
                    filterDto.getSortBy(), filterDto.getSortOrder());

            User userEntity = userService.getByIdWithComments(id, commentOptions, user);

            model.addAttribute("user", new UserCommentsPageDisplayDTO(userEntity.getId(), userEntity.getComments(), userEntity.getUsername()));
            model.addAttribute("userId", user.getId());
            model.addAttribute("currentURI", request.getRequestURI());

            return "user";
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }
    }


    @GetMapping("/{id}/settings")
    public String showUpdateUser(@PathVariable int id, HttpSession session, Model model, HttpServletRequest request) {

        try {
            User user = authenticationHelper.tryGetUserMvc(session);

            if (id != user.getId() && user.getRoles().stream().noneMatch(role -> role.getName().equalsIgnoreCase("ADMIN"))) {
                return "404";
            }
            User updatingUser = userService.getByIdWithRoles(id, user);

            boolean isAdmin = updatingUser.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"));
            UserUpdateMvcDTO userUpdate = userMapper.userToUserUpdateMvcDto(updatingUser);
            userUpdate.setAdmin(isAdmin);
            if (isAdmin) {
                userUpdate.setPhoneNumber(adminService.getByUserId(id, user).getPhoneNumber());
            }

            model.addAttribute("userUpdate", userUpdate);
            model.addAttribute("userId", user.getId());
            model.addAttribute("profilePicture", FilesHelper.checkIfPhotoExists(userUpdate.getId()));
            model.addAttribute("requestURI", request.getRequestURI());
            model.addAttribute("token", jwtUtil.generateToken(userUpdate.getUsername()));

            return "user-settings";
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }
    }

    @PutMapping("/{id}/settings")
    public String handleUpdateUser(@PathVariable int id, @Valid @ModelAttribute("userUpdate") UserUpdateMvcDTO userUpdateMvcDTO, BindingResult bindingResult,
                                   HttpSession session, Model model, @ModelAttribute("url") String url, RedirectAttributes redirectAttributes) {

        model.addAttribute("formSubmitted", true);

        if (bindingResult.hasErrors()) {
            return "user-settings";
        }

        try {
            User userRequest = authenticationHelper.tryGetUserMvc(session);

            if (userUpdateMvcDTO.getAdmin() != null) {
                AdminDetails adminToUpdate = userMapper.userUpdateMvcDtoToAdmin(id, userUpdateMvcDTO, userRequest);

                adminService.update(adminToUpdate, userRequest);

            } else {
                User userToUpdate = userMapper.userUpdateMvcDtoToUser(id, userUpdateMvcDTO, userRequest);

                userService.update(userToUpdate, userRequest);
            }

            redirectAttributes.addFlashAttribute("success", true);
            return String.format("redirect:/users/" + id + "/settings");

        } catch (DuplicateEntityException e) {
            bindingResult.rejectValue("email", "exist-email.error", e.getMessage());
            return "user-settings";
        }
    }

    @DeleteMapping("/{id}")
    public String handleDeletion(@PathVariable int id, @RequestParam boolean fromAdmin, HttpSession session, RedirectAttributes redirectAttributes) {


        User user = authenticationHelper.tryGetUserMvc(session);

        userService.delete(id, user);
        FilesHelper.deleteUserPhoto("picture" + id + ".jpg", "uploads");

        redirectAttributes.addFlashAttribute("successfulDeletion", true);

        if (fromAdmin) {
            return "redirect:/admin";
        } else {
            return "redirect:/auth/logout";
        }
    }

}
