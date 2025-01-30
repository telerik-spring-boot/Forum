package com.telerik.forum.helpers;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.InvalidUserInputException;
import com.telerik.forum.models.post.Comment;
import com.telerik.forum.models.post.Like;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.post.Tag;
import com.telerik.forum.models.dtos.commentDTOs.CommentCreateDTO;
import com.telerik.forum.models.dtos.commentDTOs.CommentDisplayDTO;
import com.telerik.forum.models.dtos.postDTOs.PostCreateDTO;
import com.telerik.forum.models.dtos.postDTOs.PostDisplayDTO;
import com.telerik.forum.services.comment.CommentService;
import com.telerik.forum.services.post.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.telerik.forum.services.comment.CommentServiceImpl.INVALID_COMMENT_ID_FOR_POST_MESSAGE;

@Component
public class PostMapper {

    private final PostService postService;
    private final CommentService commentService;


    @Autowired
    public PostMapper(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    public PostDisplayDTO postToPostDisplayDTO(Post post) {
        PostDisplayDTO postDTO = new PostDisplayDTO();

        postDTO.setTitle(post.getTitle());
        postDTO.setContent(post.getContent());
        postDTO.setCreatorUsername(post.getUser().getUsername());
        postDTO.setCreatedAt(post.getCreatedAt());

        List<String> tags = new ArrayList<>();
        for (Tag tag : post.getTags()) {
            tags.add(tag.getName());
        }
        postDTO.setTags(tags);

        int likesCount = 0;
        for (Like like : post.getLikes()) {
            likesCount += like.getReaction();
        }
        postDTO.setLikes(likesCount);

        List<CommentDisplayDTO> commentDTOS = new ArrayList<>();
        for (Comment comment : post.getComments()) {
            commentDTOS.add(commentToCommentDisplayDTO(comment));
        }
        postDTO.setComments(commentDTOS);

        return postDTO;

    }

    public CommentDisplayDTO commentToCommentDisplayDTO(Comment comment) {
        CommentDisplayDTO commentDTO = new CommentDisplayDTO();

        commentDTO.setCommentContent(comment.getContent());
        commentDTO.setCreatorUsername(comment.getUser().getUsername());
        commentDTO.setCreatedAt(comment.getCreatedAt());

        return commentDTO;
    }

    public Post dtoToPost(PostCreateDTO dto) {
        Post post = new Post();

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        return post;
    }

    public Post dtoToPost(int postId, PostCreateDTO dto) {
        Post post = postService.getById(postId);

        if (dto.getTitle() != null) {
            post.setTitle(dto.getTitle());
        }

        if (dto.getContent() != null) {
            post.setContent(dto.getContent());
        }

        return post;
    }

    public Comment dtoToComment(CommentCreateDTO dto) {
        Comment comment = new Comment();

        comment.setContent(dto.getContent());

        return comment;
    }

    public Comment dtoToComment(int commentId, CommentCreateDTO dto, int postId) {
        Comment commentToUpdate = commentService.getComment(commentId);

        if (commentToUpdate.getPost().getId() != postId) {
            throw new EntityNotFoundException("Comment with id: " + commentId + " not found for post with id:  " + postId);
        }

        if (dto.getContent() != null) {
            commentToUpdate.setContent(dto.getContent());
        }

        return commentToUpdate;
    }
}
