package com.telerik.forum.controllers.mvc;


import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.models.dtos.adminDTOs.AdminCreateDTO;
import com.telerik.forum.models.dtos.adminDTOs.AdminDisplayDTO;
import com.telerik.forum.models.dtos.adminDTOs.AdminUpdateDTO;
import com.telerik.forum.models.dtos.postDTOs.PostDisplayMvcDTO;
import com.telerik.forum.models.dtos.userDTOs.UserDisplayDTO;
import com.telerik.forum.models.dtos.userDTOs.UserDisplayMvcDTO;
import com.telerik.forum.models.user.AdminDetails;
import com.telerik.forum.models.user.User;
import com.telerik.forum.services.admin.AdminService;
import com.telerik.forum.services.post.PostService;
import com.telerik.forum.services.user.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminMvcController {

    private final AdminService adminService;
    private final PostService postService;
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;

    public AdminMvcController(AuthenticationHelper authenticationHelper, AdminService adminService, PostService postService, UserService userService) {
        this.authenticationHelper = authenticationHelper;
        this.adminService = adminService;
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping
    public String showDashboard(HttpSession session, Model model) {

        try {
            User user = authenticationHelper.tryGetUserMvc(session);

            if (!authenticationHelper.isAdmin(user)) {
                return "access-denied";
            }

            List<UserDisplayMvcDTO> users = adminService.getAllUsersMvc();
            List<LocalDateTime> postsDates = postService.getPostsCreationDates().stream().map(PostDisplayMvcDTO::getCreatedOn).toList();

            model.addAttribute("users", users);
            model.addAttribute("userId", user.getId());
            model.addAttribute("postDates", postsDates);


            return "admin";
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }
    }

    @PutMapping("/users/{userId}/grant")
    public String grantAdminRights(HttpSession session, @PathVariable int userId) {

        try {
            User user = authenticationHelper.tryGetUserMvc(session);

            if (!authenticationHelper.isAdmin(user)) {
                return "access-denied";
            }

            adminService.giveAdminRights(userId, "", user);

            return "redirect:/admin";

        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }

    }

    @PutMapping("/users/{userId}/revoke")
    public String revokeAdminRights(HttpSession session, @PathVariable int userId) {

        try {
            User user = authenticationHelper.tryGetUserMvc(session);

            if (!authenticationHelper.isAdmin(user)) {
                return "access-denied";
            }

            adminService.revokeAdminRights(userId, user);

            return "redirect:/admin";

        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }

    }

    @PutMapping("/users/{userId}/block")
    public String blockUser(HttpSession session, @PathVariable int userId) {


        try {
            User user = authenticationHelper.tryGetUserMvc(session);

            if (!authenticationHelper.isAdmin(user)) {
                return "access-denied";
            }

            User userToBlock = userService.getById(userId, user);
            adminService.blockUser(userToBlock, user);

            return "redirect:/admin";

        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }
    }

    @PutMapping("/users/{userId}/unblock")
    public String unblockUser(HttpSession session, @PathVariable int userId) {


        try {
            User user = authenticationHelper.tryGetUserMvc(session);

            if (!authenticationHelper.isAdmin(user)) {
                return "access-denied";
            }

            User userToBlock = userService.getById(userId, user);
            adminService.unblockUser(userToBlock, user);

            return "redirect:/admin";

        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }
    }

}
