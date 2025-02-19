package com.telerik.forum.controllers.mvc;


import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.helpers.UserMapper;
import com.telerik.forum.models.dtos.userDTOs.UserCommentsDisplayDTO;
import com.telerik.forum.models.dtos.userDTOs.UserCommentsPageDisplayDTO;
import com.telerik.forum.models.dtos.userDTOs.UserOverviewPageDisplayDTO;
import com.telerik.forum.models.dtos.userDTOs.UserPostsPageDisplayDTO;
import com.telerik.forum.models.filters.FilterCommentOptions;
import com.telerik.forum.models.filters.FilterPostOptions;
import com.telerik.forum.models.user.User;
import com.telerik.forum.services.user.UserService;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserMvcController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final UserMapper userMapper;

    public UserMvcController(UserService userService, AuthenticationHelper authenticationHelper, UserMapper userMapper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.userMapper = userMapper;
    }


    @GetMapping("/{id}/posts")
    public String showUserPosts(HttpSession session,
                                @RequestParam(required = false) String title,
                                @RequestParam(required = false) String content,
                                @RequestParam(required = false) String tags,
                                @RequestParam(required = false) Long minLikes,
                                @RequestParam(required = false) Long maxLikes,
                                @RequestParam(required = false) String sortBy,
                                @RequestParam(required = false) String sortOrder,
                                @PathVariable int id,
                                @PageableDefault(size = 10, page = 0) Pageable pageable,
                                Model model,
                                HttpServletRequest request) {
        try {
            User user = authenticationHelper.tryGetUserMvc(session);

            String[] tagArray = null;

            if (tags != null) {
                tagArray = tags.split(",");
            }


            FilterPostOptions options = new FilterPostOptions(null, title, content, tagArray, minLikes, maxLikes, sortBy, sortOrder);

            UserPostsPageDisplayDTO showUser = userService.getByIdWithPosts(id, options, user, pageable);

            model.addAttribute("user", showUser);
            model.addAttribute("postOptions", options);
            model.addAttribute("currentURI", request.getRequestURI());

            return "user";
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }
    }


    @GetMapping("/{id}/overview")
    public String showUserOverview(HttpSession session,
                                   @RequestParam(required = false) String content,
                                   @RequestParam(required = false) String sortBy,
                                   @RequestParam(required = false) String sortOrder,
                                   @PathVariable int id,
                                   @PageableDefault(size = 10, page = 0) Pageable pageable,
                                   Model model,
                                   HttpServletRequest request) {
        try {
            User user = authenticationHelper.tryGetUserMvc(session);

            FilterPostOptions postOptions = new FilterPostOptions(null, null, content, null, null, null, sortBy, sortOrder);
            UserOverviewPageDisplayDTO showUser = userService.getByIdWithCommentsAndPosts(id, postOptions, postOptions, user, pageable);

            model.addAttribute("user", showUser);
            model.addAttribute("filterOptions", postOptions);
            model.addAttribute("currentURI", request.getRequestURI());

            return "user";
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }
    }


    @GetMapping("/{id}/comments")
    public String getUserComments(HttpSession session,
                                  @RequestParam(required = false) String commentContent,
                                  @RequestParam(required = false) String sortBy,
                                  @RequestParam(required = false) String sortOrder,
                                  @PathVariable int id,
                                  Model model,
                                  HttpServletRequest request) {

        try {
            User user = authenticationHelper.tryGetUserMvc(session);

            FilterPostOptions commentOptions = new FilterPostOptions(null, null, commentContent, null, null, null, sortBy, sortOrder);
            User userEntity = userService.getByIdWithComments(id, commentOptions, user);

            model.addAttribute("user", new UserCommentsPageDisplayDTO(userEntity.getId(), userEntity.getComments(), userEntity.getUsername()));
            model.addAttribute("filterOptions", commentOptions);
            model.addAttribute("currentURI", request.getRequestURI());

            return "user";
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }
    }

}
