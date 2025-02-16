package com.telerik.forum.controllers.mvc;


import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.models.dtos.postDTOs.PostDisplayMvcDTO;
import com.telerik.forum.models.dtos.userDTOs.UserDisplayMvcDTO;
import com.telerik.forum.models.user.User;
import com.telerik.forum.services.admin.AdminService;
import com.telerik.forum.services.post.PostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminMvcController {

    private final AdminService adminService;
    private final PostService postService;
    private final AuthenticationHelper authenticationHelper;

    public AdminMvcController(AuthenticationHelper authenticationHelper, AdminService adminService, PostService postService) {
        this.authenticationHelper = authenticationHelper;
        this.adminService = adminService;
        this.postService = postService;
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
            model.addAttribute("postDates", postsDates);


            return "admin";
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }
    }
}
