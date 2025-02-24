package com.telerik.forum.controllers.mvc;


import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.models.dtos.postDTOs.PostDisplayDTO;
import com.telerik.forum.models.dtos.userDTOs.UserDisplayMvcDTO;
import com.telerik.forum.models.user.User;
import com.telerik.forum.services.admin.AdminService;
import com.telerik.forum.services.post.PostService;
import com.telerik.forum.services.user.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
                throw new UnauthorizedOperationException("Resource requires authentication.");
            }

            List<UserDisplayMvcDTO> users = adminService.getAllUsersMvc();
            List<LocalDateTime> postsDates = postService.getPostsCreationDates().stream().map(PostDisplayDTO::getCreatedAt).toList();

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
                return "401";
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
                return "401";
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
                return "401";
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
                return "401";
            }

            User userToBlock = userService.getById(userId, user);
            adminService.unblockUser(userToBlock, user);

            return "redirect:/admin";

        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }
    }

}
