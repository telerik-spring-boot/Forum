package com.telerik.forum.controllers.mvc;


import com.telerik.forum.exceptions.DuplicateEntityException;
import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.helpers.UserMapper;
import com.telerik.forum.models.dtos.userDTOs.UserCreateMvcDTO;
import com.telerik.forum.models.dtos.userDTOs.UserLoginDTO;
import com.telerik.forum.models.dtos.userDTOs.UserRetrieveDTO;
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
    private final UserMapper userMapper;

    public AnonymousMvcController(AuthenticationHelper authenticationHelper, UserService userService, UserMapper userMapper) {
        this.authenticationHelper = authenticationHelper;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("login", new UserLoginDTO());

        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@Valid @ModelAttribute("login") UserLoginDTO userLoginDTO, BindingResult bindingResult, HttpSession session, Model model) {

        model.addAttribute("formSubmitted", true);

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
            bindingResult.rejectValue("password", "error.login", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("register", new UserCreateMvcDTO());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("register") UserCreateMvcDTO userCreateMvcDTO, BindingResult bindingResult, Model model) {

        model.addAttribute("formSubmitted", true);

        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (!userCreateMvcDTO.getPassword().equals(userCreateMvcDTO.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.register", "Passwords do not match");
            bindingResult.rejectValue("password", "error.register", "Password do not match");
            return "register";
        }

        try {
            User user = userMapper.dtoToUser(userCreateMvcDTO);

            userService.create(user);

            return "redirect:/auth/login";

        } catch (UnauthorizedOperationException e) {
            bindingResult.rejectValue("username", "error.register", e.getMessage());
            return "register";
        } catch (DuplicateEntityException e) {
            bindingResult.rejectValue("username", "username.register", e.getMessage());
            return "register";
        }
    }


    @GetMapping("/password")
    public String showForgotPassword(Model model) {
        model.addAttribute("user", new UserRetrieveDTO());

        return "password";
    }

    @PostMapping("/password")
    public String handlePasswordRetrieval(@Valid @ModelAttribute("user") UserRetrieveDTO userRetrieveDTO, BindingResult bindingResult, Model model) {

        model.addAttribute("formSubmitted", true);

        if (bindingResult.hasErrors()) {
            return "password";
        }

        try {
            User user = userService.getByUsername(userRetrieveDTO.getUsername());

            // handle logic to send email to the user to create new password

            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            bindingResult.rejectValue("username", "error.retrieve", e.getMessage());
            return "password";
        }

    }


}
