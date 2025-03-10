package com.telerik.forum.controllers.mvc;


import com.telerik.forum.configurations.jwt.JwtUtil;
import com.telerik.forum.exceptions.DuplicateEntityException;
import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.helpers.PostMapper;
import com.telerik.forum.helpers.UserMapper;
import com.telerik.forum.models.dtos.FilterDTO;
import com.telerik.forum.models.dtos.commentDTOs.CommentCreateDTO;
import com.telerik.forum.models.dtos.commentDTOs.CommentDisplayDTO;
import com.telerik.forum.models.dtos.postDTOs.PostDisplayDTO;
import com.telerik.forum.models.dtos.userDTOs.*;
import com.telerik.forum.models.filters.FilterCommentOptions;
import com.telerik.forum.models.filters.FilterPostOptions;
import com.telerik.forum.models.filters.FilterUserOptions;
import com.telerik.forum.models.post.Comment;
import com.telerik.forum.models.post.Like;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.user.User;
import com.telerik.forum.services.admin.AdminService;
import com.telerik.forum.services.comment.CommentService;
import com.telerik.forum.services.post.PostService;
import com.telerik.forum.services.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
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
    private final AuthenticationHelper authHelper;
    private final CommentService commentService;

    public AnonymousMvcController(AuthenticationHelper authenticationHelper, UserService userService, UserMapper userMapper, JavaMailSender mailSender, JwtUtil jwtUtil, PostService postService, AdminService adminService, PostMapper postMapper, AuthenticationHelper authHelper, CommentService commentService) {
        this.authenticationHelper = authenticationHelper;
        this.userService = userService;
        this.userMapper = userMapper;
        this.mailSender = mailSender;
        this.jwtUtil = jwtUtil;
        this.postService = postService;
        this.adminService = adminService;
        this.postMapper = postMapper;
        this.authHelper = authHelper;
        this.commentService = commentService;
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


            if (user.isBlocked()) {
                bindingResult.rejectValue("username", "error.block");
                bindingResult.rejectValue("password", "error.block");
                return "login";
            }

            session.setAttribute("currentUser", userLoginDTO.getUsername());
            session.setAttribute("userId", user.getId());
            session.setAttribute("isAdmin", authenticationHelper.isAdmin(user));


            user.setLastLogin(LocalDateTime.now());
            userService.update(user, user);

            return "redirect:/home";

        } catch (UnauthorizedOperationException e) {
            bindingResult.rejectValue("username", "error.login", e.getMessage());
            bindingResult.rejectValue("password", "error.login", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/auth/logout")
    public String handleLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/home";
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
    public String handlePasswordRetrieval(@Valid @ModelAttribute("user") UserRetrieveDTO userRetrieveDTO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        model.addAttribute("formSubmitted", true);

        if (bindingResult.hasErrors()) {
            return "password";
        }

        try {
            User user = userService.getByUsername(userRetrieveDTO.getUsername());

            SimpleMailMessage mailMessage = new SimpleMailMessage();

            String token = jwtUtil.generateToken(user.getUsername());

            String resetUrl = request.getScheme() + "://" + request.getServerName() + "/auth/reset-password?token=" + token;

            mailMessage.setTo(user.getEmailAddress());
            mailMessage.setSubject("Password retrieval");
            mailMessage.setText("Hello, " + user.getUsername() + " to reset your password, click on the link: " + resetUrl);
            mailMessage.setFrom("roamify55@yahoo.com");

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

    @GetMapping("/home")
    public String showPostSearchPage(@ModelAttribute("searchTerm") String searchTerm,
                                     @ModelAttribute("filterOptions") FilterDTO filterDto,
                                     HttpSession session, Model model,
                                     HttpServletRequest request) {
        boolean isAuthenticated = false;
        if (session.getAttribute("currentUser") != null) {
            model.addAttribute("userId", userService.getByUsername((String) session.getAttribute("currentUser")).getId());

            isAuthenticated = true;
        }


        String[] tagArray = null;

        if (filterDto.getTags() != null) {
            tagArray = filterDto.getTags().split(",");
        }

        List<Post> totalFoundPosts;

        if (filterDto.getContent() == null && filterDto.getTitle() == null) {

            FilterPostOptions filterPostOptions = new FilterPostOptions(filterDto.getCreatorUsername(),
                    searchTerm, null, tagArray, filterDto.getMinLikes(), filterDto.getMaxLikes(),
                    filterDto.getSortBy(), filterDto.getSortOrder());
            List<Post> foundPosts = postService.getAllPostsWithFilters(filterPostOptions);

            filterPostOptions = new FilterPostOptions(filterDto.getCreatorUsername(),
                    null, searchTerm, tagArray, filterDto.getMinLikes(), filterDto.getMaxLikes(),
                    filterDto.getSortBy(), filterDto.getSortOrder());
            List<Post> foundPostsContent = postService.getAllPostsWithFilters(filterPostOptions);

            totalFoundPosts = Stream.concat(foundPosts.stream(), foundPostsContent.stream())
                    .distinct()
                    .collect(Collectors.toList());

        } else {
            FilterPostOptions filterPostOptions = new FilterPostOptions(filterDto.getCreatorUsername(),
                    filterDto.getTitle(), filterDto.getContent(), tagArray, filterDto.getMinLikes(), filterDto.getMaxLikes(),
                    filterDto.getSortBy(), filterDto.getSortOrder());
            totalFoundPosts = postService.getAllPostsWithFilters(filterPostOptions);

        }

        if (filterDto.getSortBy() == null && filterDto.getSortOrder() == null) {
            totalFoundPosts.sort(Comparator.comparing(Post::getCreatedAt).reversed());
        }

        List<PostDisplayDTO> totalPostDTOs = totalFoundPosts.stream()
                .map(postMapper::postToPostDisplayDTO)
                .toList();

        if (isAuthenticated) {

            User user = authenticationHelper.tryGetUserMvc(session);

            for (int i = 0; i < totalFoundPosts.size(); i++) {

                for (Like like : totalFoundPosts.get(i).getLikes()) {
                    if (like.getUser().getId() == user.getId()) {
                        totalPostDTOs.get(i).setReaction(like.getReaction());
                        break;
                    }
                }
            }
        }


        model.addAttribute("foundPosts", totalPostDTOs);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("currentURI", request.getRequestURI());
        model.addAttribute("comment", new CommentCreateDTO());


        return "home";
    }


    @GetMapping("/home/comments")
    public String showCommentsSearchPage(@ModelAttribute("searchTerm") String searchTerm,
                                         @ModelAttribute("filterOptions") FilterDTO filterDto,
                                         HttpSession session, Model model,
                                         HttpServletRequest request) {

        if (session.getAttribute("currentUser") != null) {
            model.addAttribute("userId", userService.getByUsername((String) session.getAttribute("currentUser")).getId());

        }

        String search = searchTerm;
        if (filterDto.getContent() != null) {
            search = filterDto.getContent();
        }

        FilterCommentOptions filterCommentOptions = new FilterCommentOptions(filterDto.getCreatorUsername(), search,
                filterDto.getSortBy(), filterDto.getSortOrder());

        List<Comment> foundComments = commentService.getAllComments(filterCommentOptions);

        if (filterDto.getSortBy() == null && filterDto.getSortOrder() == null) {
            foundComments.sort(Comparator.comparing(Comment::getCreatedAt).reversed());
        }

        List<CommentDisplayDTO> totalCommentDTOs = foundComments.stream()
                .map(postMapper::commentToCommentDisplayDTO)
                .toList();

        model.addAttribute("foundComments", totalCommentDTOs);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("currentURI", request.getRequestURI());


        return "home";
    }

    @GetMapping("/home/users")
    public String showUserSearchPage(@ModelAttribute("searchTerm") String searchTerm,
                                     @ModelAttribute("filterOptions") FilterDTO filterDto,
                                     @PageableDefault(size = Integer.MAX_VALUE, page = 0) Pageable pageable,
                                     HttpSession session, Model model,
                                     HttpServletRequest request) {
        User user;
        try {
            user = authHelper.tryGetUserMvc(session);
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }

        String search = searchTerm;
        if (filterDto.getUsername() != null) {
            search = filterDto.getUsername();
        }

        FilterUserOptions filterUserOptions = new FilterUserOptions(search,
                null, filterDto.getFirstName(), filterDto.getLastName(),
                filterDto.getSortBy(), filterDto.getSortOrder());
        Page<User> foundUsers = adminService.getAllUsers(filterUserOptions, user, pageable);
        List<User> foundUsersList = new ArrayList<>(foundUsers.getContent());

        if (filterDto.getSortBy() == null && filterDto.getSortOrder() == null) {
            foundUsersList.sort(Comparator.comparing(User::getLastLogin).reversed());
        }

        List<UserDisplayMvcDTO> totalUsersDTOs = foundUsersList.stream()
                .map(userMapper::userToUserDisplayMVCDTO)
                .toList();


        model.addAttribute("foundUsers", totalUsersDTOs);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("currentURI", request.getRequestURI());
        model.addAttribute("userId", user.getId());

        return "home";
    }

    @GetMapping("/privacy")
    public String getPrivacyPage() {
        return "privacy-policy";
    }

    @GetMapping("/terms")
    public String getTermsAndConditions() {
        return "terms-and-conditions";
    }

    @GetMapping("/help")
    public String getHelpView() {
        return "help";
    }

    @GetMapping("/careers")
    public String getCareers() {
        return "careers";
    }

    @GetMapping("/about")
    public String getAbout() {
        return "about";
    }


    @GetMapping()
    public String redirectToHome() {
        return "redirect:/home";
    }

}
