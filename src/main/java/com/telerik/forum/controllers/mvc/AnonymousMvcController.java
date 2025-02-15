package com.telerik.forum.controllers.mvc;


import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.models.dtos.userDTOs.UserLoginDTO;
import com.telerik.forum.models.user.User;
import com.telerik.forum.services.user.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AnonymousMvcController {

    private final AuthenticationHelper authenticationHelper;
    private final UserService userService;

    public AnonymousMvcController(AuthenticationHelper authenticationHelper, UserService userService) {
        this.authenticationHelper = authenticationHelper;
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("login", new UserLoginDTO());

        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@Valid @ModelAttribute("login") UserLoginDTO userLoginDTO, BindingResult bindingResult, HttpSession session) {

        if (bindingResult.hasErrors()) {
            return "login";
        }

        try {
            User user = authenticationHelper.verifyAuthentication(userLoginDTO.getUsername(), userLoginDTO.getPassword());

            session.setAttribute("currentUser", userLoginDTO.getUsername());
            session.setAttribute("isAdmin", authenticationHelper.isAdmin(user));

            return "redirect:/home";

        } catch (UnauthorizedOperationException e) {
            bindingResult.rejectValue("username", "error.login", e.getMessage());
            return "login";
        }
    }


}
