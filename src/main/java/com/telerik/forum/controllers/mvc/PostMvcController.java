package com.telerik.forum.controllers.mvc;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.helpers.PostMapper;
import com.telerik.forum.models.dtos.commentDTOs.CommentCreateDTO;
import com.telerik.forum.models.dtos.postDTOs.PostDisplayDTO;
import com.telerik.forum.models.post.Comment;
import com.telerik.forum.models.post.Like;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.user.User;
import com.telerik.forum.services.comment.CommentService;
import com.telerik.forum.services.like.LikeService;
import com.telerik.forum.services.post.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/posts")
public class PostMvcController {

    private final PostService postService;
    private final AuthenticationHelper authHelper;
    private final PostMapper postMapper;
    private final LikeService likeService;
    private final CommentService commentService;

    @Autowired
    public PostMvcController(PostService postService, AuthenticationHelper authHelper, PostMapper postMapper,
                             LikeService likeService, CommentService commentService) {
        this.postService = postService;
        this.authHelper = authHelper;
        this.postMapper = postMapper;
        this.likeService = likeService;
        this.commentService = commentService;
    }

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @PostMapping("/{id}")
    public String postComment(@PathVariable("id") int postId,
                              @Valid @ModelAttribute("commentCreateDto") CommentCreateDTO commentCreateDTO,
                              BindingResult bindingResult, Model model, HttpSession session) {

        User user;
        try {
            user = authHelper.tryGetUserMvc(session);
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) {
            extracted(postId,model, user);
            return "PostView";
        }

        try{
            Comment comment = postMapper.dtoToComment(commentCreateDTO);
            commentService.addComment(postId, comment, user);
            return "redirect:/posts/" + postId;
        }catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
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

            extracted(id, model, user);
            model.addAttribute("commentCreateDto", new CommentCreateDTO());

            return "PostView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    private void extracted(int id, Model model, User user) {
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
