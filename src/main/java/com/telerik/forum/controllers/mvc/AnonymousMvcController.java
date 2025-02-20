package com.telerik.forum.controllers.mvc;


import com.telerik.forum.configurations.jwt.JwtUtil;
import com.telerik.forum.exceptions.DuplicateEntityException;
import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.helpers.PostMapper;
import com.telerik.forum.helpers.UserMapper;
import com.telerik.forum.models.dtos.PaginationDTO;
import com.telerik.forum.models.dtos.postDTOs.PostDisplayDTO;
import com.telerik.forum.models.dtos.userDTOs.UserCreateMvcDTO;
import com.telerik.forum.models.dtos.userDTOs.UserLoginDTO;
import com.telerik.forum.models.dtos.userDTOs.UserPasswordUpdateDTO;
import com.telerik.forum.models.dtos.userDTOs.UserRetrieveDTO;
import com.telerik.forum.models.filters.FilterPostOptions;
import com.telerik.forum.models.filters.FilterUserOptions;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.user.User;
import com.telerik.forum.services.admin.AdminService;
import com.telerik.forum.services.post.PostService;
import com.telerik.forum.services.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Controller
@RequestMapping
public class AnonymousMvcController {

    private final AuthenticationHelper authenticationHelper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final JavaMailSender mailSender;
    private final JwtUtil jwtUtil;
    private final PostService postService;
    private final AdminService adminService;
    private final PostMapper postMapper;

    public AnonymousMvcController(AuthenticationHelper authenticationHelper, UserService userService, UserMapper userMapper, JavaMailSender mailSender, JwtUtil jwtUtil, PostService postService, AdminService adminService, PostMapper postMapper) {
        this.authenticationHelper = authenticationHelper;
        this.userService = userService;
        this.userMapper = userMapper;
        this.mailSender = mailSender;
        this.jwtUtil = jwtUtil;
        this.postService = postService;
        this.adminService = adminService;
        this.postMapper = postMapper;
    }

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/auth/login")
    public String showLoginPage(HttpSession session, Model model) {

        if (session.getAttribute("currentUser") != null) {
            return "redirect:/home";
        }

        model.addAttribute("login", new UserLoginDTO());

        return "login";
    }

    @PostMapping("/auth/login")
    public String handleLogin(@Valid @ModelAttribute("login") UserLoginDTO userLoginDTO, BindingResult bindingResult, HttpSession session, Model model) {

        model.addAttribute("formSubmitted", true);

        if (bindingResult.hasErrors()) {
            return "login";
        }

        try {
            User user = authenticationHelper.verifyAuthentication(userLoginDTO.getUsername(), userLoginDTO.getPassword());

            session.setAttribute("currentUser", userLoginDTO.getUsername());
            session.setAttribute("userId", user.getId());
            session.setAttribute("isAdmin", authenticationHelper.isAdmin(user));

            user.setLastLogin(LocalDateTime.now());
            userService.update(user, user);

            return "redirect:/search";

        } catch (UnauthorizedOperationException e) {
            bindingResult.rejectValue("username", "error.login", e.getMessage());
            bindingResult.rejectValue("password", "error.login", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/auth/logout")
    public String handleLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/search";
    }

    @GetMapping("/auth/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("register", new UserCreateMvcDTO());
        return "register";
    }

    @PostMapping("/auth/register")
    public String handleRegister(@Valid @ModelAttribute("register") UserCreateMvcDTO userCreateMvcDTO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

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

            redirectAttributes.addFlashAttribute("registerSuccess", true);
            return "redirect:/auth/login";

        } catch (UnauthorizedOperationException e) {
            bindingResult.rejectValue("username", "error.register", e.getMessage());
            return "register";
        } catch (DuplicateEntityException e) {
            bindingResult.rejectValue("username", "username.register", e.getMessage());
            return "register";
        }
    }


    @GetMapping("/auth/request-password")
    public String showForgotPassword(Model model) {
        model.addAttribute("user", new UserRetrieveDTO());

        return "password";
    }

    @PostMapping("/auth/request-password")
    public String handlePasswordRetrieval(@Valid @ModelAttribute("user") UserRetrieveDTO userRetrieveDTO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        model.addAttribute("formSubmitted", true);

        if (bindingResult.hasErrors()) {
            return "password";
        }

        try {
            User user = userService.getByUsername(userRetrieveDTO.getUsername());

            SimpleMailMessage mailMessage = new SimpleMailMessage();

            String token = jwtUtil.generateToken(user.getUsername());
            String resetUrl = "http://localhost:8080/auth/reset-password?token=" + token;

            mailMessage.setTo(user.getEmailAddress());
            mailMessage.setSubject("Password retrieval");
            mailMessage.setText("Hello, " + user.getUsername() + " to reset your password, click on the link: " + resetUrl);
            mailMessage.setFrom("norep-forum@outlook.com");

            mailSender.send(mailMessage);

            redirectAttributes.addFlashAttribute("emailSuccess", true);

            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            bindingResult.rejectValue("username", "error.retrieve", e.getMessage());
            return "password";
        }

    }

    @GetMapping("/auth/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {

        try {
            jwtUtil.extractUsername(token);
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }

        model.addAttribute("token", token);
        model.addAttribute("user", new UserPasswordUpdateDTO());

        return "reset-password";
    }

    @PostMapping("/auth/reset-password")
    public String resetPassword(@RequestParam String token, @Valid @ModelAttribute("user") UserPasswordUpdateDTO userPasswordUpdateDTO, BindingResult bindingResult, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {

        model.addAttribute("formSubmitted", true);

        if (bindingResult.hasErrors()) {
            return "reset-password";
        }

        if (!userPasswordUpdateDTO.getPassword().equals(userPasswordUpdateDTO.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.register", "Passwords do not match");
            bindingResult.rejectValue("password", "error.register", "Password do not match");
            return "reset-password";
        }


        try {
            User user = userService.getByUsernameWithRoles(jwtUtil.extractUsername(token));

            user.setPassword(new BCryptPasswordEncoder().encode(userPasswordUpdateDTO.getPassword()));

            userService.update(user, user);

            model.addAttribute("currentURI", request.getRequestURI());

            redirectAttributes.addFlashAttribute("changeSuccess", true);

            return "redirect:/auth/login";

        } catch (Exception e) {
            bindingResult.rejectValue("password", "error.reset", e.getMessage());
            bindingResult.rejectValue("confirmPassword", "error.reset", e.getMessage());
            return "reset-password";
        }

    }

    @GetMapping("/search")
    public String showPostSearchPage(@ModelAttribute("searchTerm") String searchTerm, HttpSession session, Model model) {
        if (session.getAttribute("currentUser") != null) {
            model.addAttribute("userId", userService.getByUsername((String) session.getAttribute("currentUser")).getId());
        }
        FilterPostOptions filterPostOptions = new FilterPostOptions(null,
                searchTerm, null, null, null, null, null, null);
        List<Post> foundPosts = postService.getAllPostsWithFilters(filterPostOptions);

        filterPostOptions = new FilterPostOptions(null,
                null, searchTerm, null, null, null, null, null);
        List<Post> foundPostsContent = postService.getAllPostsWithFilters(filterPostOptions);

        List<Post> totalFoundPosts = Stream.concat(foundPosts.stream(), foundPostsContent.stream())
                .distinct()
                .toList();

        List<PostDisplayDTO> totalPostDTOs = totalFoundPosts.stream()
                .map(postMapper::postToPostDisplayDTO)
                .toList();

        model.addAttribute("foundPosts", totalPostDTOs);
        model.addAttribute("searchTerm", searchTerm);

//        return "search";
        return "search-updated";
    }

//    @GetMapping("/search/users")
//    public String showUserSearchPage(@ModelAttribute("searchString") String searchTerm, HttpSession session, Model model) {
//        if (session.getAttribute("currentUser") != null) {
//            model.addAttribute("userId", userService.getByUsername((String) session.getAttribute("currentUser")).getId());
//        }
//        FilterUserOptions filterUserOptions = new FilterUserOptions(searchTerm,
//                 null, null, null, null);
//        List<User> foundUsers = adminService.getAllUsers(filterUserOptions);
//
//        model.addAttribute("foundUsers", foundUsers);
//        model.addAttribute("searchTerm", searchTerm);
//
//        return "search";
//    }


}
