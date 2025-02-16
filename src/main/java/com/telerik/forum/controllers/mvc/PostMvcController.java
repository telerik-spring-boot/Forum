package com.telerik.forum.controllers.mvc;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.helpers.PostMapper;
import com.telerik.forum.models.dtos.postDTOs.PostDisplayDTO;
import com.telerik.forum.models.post.Like;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.user.User;
import com.telerik.forum.services.like.LikeService;
import com.telerik.forum.services.post.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/posts")
public class PostMvcController {

    private final PostService postService;
    private final AuthenticationHelper authHelper;
    private final PostMapper postMapper;
    private final LikeService likeService;

    @Autowired
    public PostMvcController(PostService postService, AuthenticationHelper authHelper, PostMapper postMapper, LikeService likeService) {
        this.postService = postService;
        this.authHelper = authHelper;
        this.postMapper = postMapper;
        this.likeService = likeService;
    }

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }


    @GetMapping("/{id}")
    public String getSinglePost(@PathVariable int id, Model model,
                                HttpSession session) {
        User user;
        try {
            user = authHelper.tryGetUserMvc(session);
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }

        try {
            Post post = postService.getByIdWithCommentsAndLikesAndTags(id);
            PostDisplayDTO postToDisplay = postMapper.postToPostDisplayDTO(post);
            model.addAttribute("post", postToDisplay);

            int userReaction = 0;
            for (Like like : post.getLikes()) {
                if (like.getUser().getId() == user.getId()) {
                    userReaction = like.getReaction();
                    break;
                }
            }
            model.addAttribute("userReaction", userReaction);

            return "PostView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/{id}/like")
    public String likePost(@PathVariable int id, Model model,
                           HttpSession session) {
        User user;
        try {
            user = authHelper.tryGetUserMvc(session);
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }

        try {
            likeService.likePost(id, user);
            return "redirect:/posts/" + id;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/{id}/dislike")
    public String dislikePost(@PathVariable int id, Model model,
                              HttpSession session) {
        User user;
        try {
            user = authHelper.tryGetUserMvc(session);
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }

        try {
            likeService.dislikePost(id, user);
            return "redirect:/posts/" + id;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }


}
