package com.telerik.forum.controllers.mvc;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.helpers.PostMapper;
import com.telerik.forum.models.dtos.commentDTOs.CommentCreateDTO;
import com.telerik.forum.models.dtos.postDTOs.PostCreateDTO;
import com.telerik.forum.models.dtos.postDTOs.PostDisplayDTO;
import com.telerik.forum.models.dtos.postDTOs.PostUpdateDTO;
import com.telerik.forum.models.dtos.tagDTOs.TagCreateAndDeleteMvcDTO;
import com.telerik.forum.models.post.Comment;
import com.telerik.forum.models.post.Like;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.user.User;
import com.telerik.forum.services.comment.CommentService;
import com.telerik.forum.services.like.LikeService;
import com.telerik.forum.services.post.PostService;
import com.telerik.forum.services.tag.TagService;
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
    private final TagService tagService;

    @Autowired
    public PostMvcController(PostService postService, AuthenticationHelper authHelper, PostMapper postMapper,
                             LikeService likeService, CommentService commentService, TagService tagService) {
        this.postService = postService;
        this.authHelper = authHelper;
        this.postMapper = postMapper;
        this.likeService = likeService;
        this.commentService = commentService;
        this.tagService = tagService;
    }

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @PostMapping("/{id}")
    public String postComment(@PathVariable("id") int postId,
                              @Valid @ModelAttribute("commentCreateDto") CommentCreateDTO commentCreateDTO,
                              BindingResult bindingResult, Model model, HttpSession session,
                              @RequestParam(required = false) String url) {

        User user;
        try {
            user = authHelper.tryGetUserMvc(session);
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) {
            extracted(postId, model, user);
//            return "single-post-updated";
            return "single-page-updated-thin";
        }

        try {
            Comment comment = postMapper.dtoToComment(commentCreateDTO);
            commentService.addComment(postId, comment, user);
            if (url != null) {
                return "redirect:" + url;
            }
            return "redirect:/posts/" + postId;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "404";
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
            model.addAttribute("userId", user.getId());

            //return "single-post-updated";
            return "single-page-updated-thin";

        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "404";
        }
    }

    private void extracted(int id, Model model, User user) {
        Post post = postService.getByIdWithCommentsAndLikesAndTags(id);
        PostDisplayDTO postToDisplay = postMapper.postToPostDisplayDTO(post);
        model.addAttribute("post", postToDisplay);

//        if (post.getUser().equals(user)) {
//            model.addAttribute("userIsCreator",true);
//        }else{
//            model.addAttribute("userIsCreator",false);
//        }
        model.addAttribute("sessionUserId", user.getId());

        if(user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN")))
        {
            model.addAttribute("userIsAdmin",true);
        }else{
            model.addAttribute("userIsAdmin",false);
        }

        if(user.isBlocked())
        {
            model.addAttribute("userIsBlocked",true);
        }else {
            model.addAttribute("userIsBlocked",false);
        }

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
                           HttpSession session, @RequestParam(required = false) String url) {
        User user;
        try {
            user = authHelper.tryGetUserMvc(session);
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }

        try {
            likeService.likePost(id, user);
            if (url != null) {
                return "redirect:" + url;
            } else return "redirect:/posts/" + id;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "404";
        }
    }

    @GetMapping("/{id}/dislike")
    public String dislikePost(@PathVariable int id, Model model,
                              HttpSession session, @RequestParam(required = false) String url) {
        User user;
        try {
            user = authHelper.tryGetUserMvc(session);
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }

        try {
            likeService.dislikePost(id, user);
            if (url != null) {
                return "redirect:" + url;
            }
            return "redirect:/posts/" + id;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "404";
        }
    }

    @GetMapping()
    public String showNewPostForm(Model model, HttpSession session) {

        try {
            authHelper.tryGetUserMvc(session);
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }

        model.addAttribute("postCreateDTO", new PostCreateDTO());
        model.addAttribute("tagCreateDTO", new TagCreateAndDeleteMvcDTO());
        return "create-post";
    }

    @PostMapping()
    public String createPost(@Valid @ModelAttribute("postCreateDTO") PostCreateDTO postCreateDTO,
                             BindingResult bindingResult,
                             @Valid @ModelAttribute("tagCreateDTO") TagCreateAndDeleteMvcDTO tagCreateDTO,
                             BindingResult tagBindingResult,
                             Model model, HttpSession session) {

        User user;
        try {
            user = authHelper.tryGetUserMvc(session);
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) {
            return "create-post";
        }

        if (tagBindingResult.hasErrors()) {
            return "create-post";
        }

        try {
            Post post = postMapper.dtoToPost(postCreateDTO);
            postService.createPost(post, user);

            tagService.addTagToPost(post.getId(), tagCreateDTO.getTags(), user);

            return "redirect:/posts/" + post.getId();
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "404";
        }

    }

    @GetMapping("/{id}/update")
    public String showUpdatePostForm(@PathVariable int id, Model model, HttpSession session) {
        try {
            authHelper.tryGetUserMvc(session);
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }

        try {
            Post post = postService.getByIdWithCommentsAndLikesAndTags(id);
            PostUpdateDTO postToDisplay = postMapper.postToPostUpdateDTO(post);

            model.addAttribute("postUpdateDTO", postToDisplay);

            session.setAttribute("oldTags", postToDisplay.getTags());

            return "update-post";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "404";
        }
    }

    @PostMapping("/{id}/update")
    public String updatePost(@PathVariable int id,
                             @Valid @ModelAttribute("postUpdateDTO") PostUpdateDTO postUpdateDTO,
                             BindingResult bindingResult,
                             Model model, HttpSession session) {

        User user;
        try {
            user = authHelper.tryGetUserMvc(session);
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) {
            return "update-post";
        }

        try {
            PostCreateDTO postCreateDTO = new PostCreateDTO();
            postCreateDTO.setTitle(postUpdateDTO.getTitle());
            postCreateDTO.setContent(postUpdateDTO.getContent());
            Post post = postMapper.dtoToPost(id, postCreateDTO);

            postService.updatePost(post, user);

            String oldTags = "";
            if (session.getAttribute("oldTags") != null) {
                oldTags = (String) session.getAttribute("oldTags");
            }

            tagService.updateTagFromPost(id, oldTags, postUpdateDTO.getTags(), user);
            return "redirect:/posts/" + id;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "404";
        }catch(UnauthorizedOperationException e){
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "401";
        }

    }

    @GetMapping("/{id}/delete")
    public String deletePost(@PathVariable int id, HttpSession session, Model model) {

        User user;
        try {
            user = authHelper.tryGetUserMvc(session);
        } catch (UnauthorizedOperationException e) {
            return "redirect:/auth/login";
        }

        try{
            postService.deletePost(id, user);
            return "redirect:/home";
        }catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "404";
        }catch(UnauthorizedOperationException e){
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "401";
        }
    }


}
