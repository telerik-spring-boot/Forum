package com.telerik.forum.helpers;

import com.telerik.forum.models.Comment;
import com.telerik.forum.models.Post;
import com.telerik.forum.models.dtos.CommentDisplayDTO;
import com.telerik.forum.models.dtos.PostCreateDTO;
import com.telerik.forum.models.dtos.PostDisplayDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class PostMapper {

    public PostDisplayDTO postToPostDisplayDTO(Post post) {
        PostDisplayDTO postDTO = new PostDisplayDTO();

        postDTO.setTitle(post.getTitle());
        postDTO.setContent(post.getContent());
        postDTO.setCreatorUsername(post.getUser().getUsername());
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

    public Post dtoToPost(PostCreateDTO dto){
        Post post = new Post();

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        return post;
    }

}
