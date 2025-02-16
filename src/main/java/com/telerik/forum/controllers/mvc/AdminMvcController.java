package com.telerik.forum.controllers.mvc;


import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.models.dtos.userDTOs.UserDisplayMvcDTO;
import com.telerik.forum.models.user.User;
import com.telerik.forum.services.admin.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminMvcController {

    private final AdminService adminService;
    private AuthenticationHelper authenticationHelper;

    public AdminMvcController(AuthenticationHelper authenticationHelper, AdminService adminService) {
        this.authenticationHelper = authenticationHelper;
        this.adminService = adminService;
    }

    @GetMapping
    public String showDashboard(HttpSession session, Model model) {

        try {
            User user = authenticationHelper.tryGetUserMvc(session);

            if (!authenticationHelper.isAdmin(user)) {
                return "access-denied";
            }

            List<UserDisplayMvcDTO> users = adminService.getAllUsersMvc();
            model.addAttribute("users", users);


            return "admin";
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }
    }
}
