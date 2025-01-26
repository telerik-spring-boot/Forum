package com.telerik.forum.helpers;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.models.Comment;
import com.telerik.forum.models.Like;
import com.telerik.forum.models.Post;
import com.telerik.forum.models.Tag;
import com.telerik.forum.models.dtos.commentDTOs.CommentCreateDTO;
import com.telerik.forum.models.dtos.commentDTOs.CommentDisplayDTO;
import com.telerik.forum.models.dtos.postDTOs.PostCreateDTO;
import com.telerik.forum.models.dtos.postDTOs.PostDisplayDTO;
import com.telerik.forum.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PostMapper {

    private final PostService postService;


    @Autowired
    public PostMapper(PostService postService) {
        this.postService = postService;
    }

    public PostDisplayDTO postToPostDisplayDTO(Post post) {
        PostDisplayDTO postDTO = new PostDisplayDTO();

        postDTO.setTitle(post.getTitle());
        postDTO.setContent(post.getContent());
        postDTO.setCreatorUsername(post.getUser().getUsername());

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
        Post post = postService.getByIdWithComments(postId);
        int commentSize = post.getComments().size();
        if (commentId > commentSize) {
            throw new EntityNotFoundException("Comment", "id", commentId);
        }
        //Comment commentToUpdate = post.getComments().get(commentId - 1);
        Comment commentToUpdate = post.getComments().stream()
                .toList().get(commentId - 1);

        if (dto.getContent() != null) {
            commentToUpdate.setContent(dto.getContent());
        }

        return commentToUpdate;
    }

}
