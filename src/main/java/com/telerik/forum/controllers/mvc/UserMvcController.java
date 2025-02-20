package com.telerik.forum.controllers.mvc;


import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.helpers.UserMapper;
import com.telerik.forum.models.dtos.FilterDTO;
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

    public UserMvcController(UserService userService, AuthenticationHelper authenticationHelper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
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
            model.addAttribute("currentURI", request.getRequestURI());

            return "user";
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }
    }

}
