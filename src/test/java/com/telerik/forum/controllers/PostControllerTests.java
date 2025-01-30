package com.telerik.forum.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.InvalidSortParameterException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.helpers.PostMapper;
import com.telerik.forum.models.dtos.commentDTOs.CommentCreateDTO;
import com.telerik.forum.models.dtos.postDTOs.PostCreateDTO;
import com.telerik.forum.models.dtos.postDTOs.PostDisplayDTO;
import com.telerik.forum.models.dtos.tagDTOs.TagCreateAndDeleteDTO;
import com.telerik.forum.models.dtos.tagDTOs.TagUpdateDTO;
import com.telerik.forum.services.comment.CommentService;
import com.telerik.forum.services.like.LikeService;
import com.telerik.forum.services.post.PostService;
import com.telerik.forum.services.tag.TagService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static com.telerik.forum.DummyObjectProvider.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(controllers = PostController.class)
public class PostControllerTests {

    @MockitoBean
    private PostService mockPostService;

    @MockitoBean
    private AuthenticationHelper mockAuthenticationHelper;

    @MockitoBean
    private PostMapper mockPostMapper;

    @MockitoBean
    private CommentService mockCommentService;

    @MockitoBean
    private LikeService mockLikeService;

    @MockitoBean
    private TagService mockTagService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getAllPosts_Should_ReturnStatusOk_When_ParametersAreValid() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(createMockUser());


        Mockito.when(mockPostService.getAllPostsWithFilters(Mockito.any()))
                .thenReturn(List.of(createMockPost()));

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts")
                        .param("tags", "tag,two"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType("application/json"));

    }

    @Test
    public void getAllPosts_Should_ReturnStatusBadRequest_When_InvalidSortingParameter() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(createMockUser());

        Mockito.when(mockPostService.getAllPostsWithFilters(Mockito.any()))
                .thenThrow(InvalidSortParameterException.class);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    public void getAllPosts_Should_ReturnStatusUnauthorized_When_UserIsNotAuthorized() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenThrow(UnauthorizedOperationException.class);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void getPostById_Should_ReturnStatusOk_When_UserAuthorizedAndPostExists() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(createMockUser());

        Mockito.when(mockPostService.getByIdWithCommentsAndLikesAndTags(Mockito.anyInt()))
                .thenReturn(createMockPost());

        Mockito.when(mockPostMapper.postToPostDisplayDTO(Mockito.any()))
                .thenReturn(new PostDisplayDTO());

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    public void getPostById_Should_ThrowStatusUnauthorized_When_UserNotAuthorized() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenThrow(UnauthorizedOperationException.class);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/1"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void getPostById_Should_ThrowStatusNotFound_When_PostDoesNotExist() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(createMockUser());

        Mockito.when(mockPostService.getByIdWithCommentsAndLikesAndTags(Mockito.anyInt()))
                .thenThrow(EntityNotFoundException.class);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void createPost_Should_ReturnStatusOk_When_UserAuthorizedAndNotBlocked() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(createMockAdminDetails().getUser());

        Mockito.when(mockPostMapper.dtoToPost(Mockito.any()))
                .thenReturn(createMockPost());

        Mockito.doNothing().when(mockPostService)
                .createPost(Mockito.any(), Mockito.any());

        Mockito.when(mockPostMapper.postToPostDisplayDTO(Mockito.any()))
                .thenReturn(new PostDisplayDTO());


        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createMockPostCreateDTO())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType("application/json"));

    }

    @Test
    public void createPost_Should_ReturnStatusBadRequest_When_RequestBodyInvalid() throws Exception {
        // Arrange
        PostCreateDTO postCreateDTO = createMockPostCreateDTO();
        postCreateDTO.setContent("invalid");

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(postCreateDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    public void createPost_Should_Return_StatusUnauthorized_When_ValidRequestBodyButUnauthorized() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenThrow(UnauthorizedOperationException.class);


        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createMockPostCreateDTO())))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

    }

    @Test
    public void addCommentToPost_Should_ReturnStatusOk_When_UserAuthorizedAndPostExists() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(createMockUser());

        Mockito.when(mockPostMapper.dtoToComment(Mockito.any()))
                .thenReturn(createMockComment());

        Mockito.when(mockPostService.getByIdWithCommentsAndLikesAndTags(Mockito.anyInt()))
                .thenReturn(createMockPost());

        Mockito.when(mockPostMapper.postToPostDisplayDTO(Mockito.any()))
                .thenReturn(new PostDisplayDTO());

        CommentCreateDTO commentCreateDTO = new CommentCreateDTO("Valid content length and everything is fine.");

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts/1/comments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentCreateDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType("application/json"));


    }

    @Test
    public void addCommentToPost_Should_ReturnStatusBadRequest_When_BodyIsInvalid() throws Exception {
        // Arrange, Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts/1/comments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new CommentCreateDTO())))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void addCommentToPost_Should_ReturnStatusNotFound_When_PostNotFound() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(createMockUser());

        Mockito.when(mockPostMapper.dtoToComment(Mockito.any()))
                .thenReturn(createMockComment());

        Mockito.doThrow(EntityNotFoundException.class)
                .when(mockCommentService).addComment(Mockito.anyInt(), Mockito.any(), Mockito.any());

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts/1/comments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new CommentCreateDTO("valid comment content"))))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void addCommentToPost_Should_ReturnStatusUnauthorized_When_UserNotAuthorized() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenThrow(UnauthorizedOperationException.class);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts/1/comments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new CommentCreateDTO("Valid content"))))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }


    @Test
    public void addTagsToPost_Should_ReturnStatusOk_When_UserAuthorizedAndPostExists() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(createMockUser());

        Mockito.when(mockPostService.getByIdWithCommentsAndLikesAndTags(Mockito.anyInt()))
                .thenReturn(createMockPost());

        Mockito.when(mockPostMapper.postToPostDisplayDTO(Mockito.any()))
                .thenReturn(new PostDisplayDTO());

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts/1/tags")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new TagCreateAndDeleteDTO("tag,two"))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType("application/json"));


    }

    @Test
    public void addTagsToPost_Should_ReturnStatusBadRequest_When_BodyIsInvalid() throws Exception {
        // Arrange, Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts/1/tags")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new TagCreateAndDeleteDTO())))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void addTagsToPost_Should_ReturnStatusNotFound_When_PostNotFound() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(createMockUser());

        Mockito.doThrow(EntityNotFoundException.class).when(mockTagService)
                .addTagToPost(Mockito.anyInt(), Mockito.any(), Mockito.any());

        Mockito.doThrow(EntityNotFoundException.class)
                .when(mockCommentService).addComment(Mockito.anyInt(), Mockito.any(), Mockito.any());

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts/1/tags")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new TagCreateAndDeleteDTO("valid,tag"))))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void addTagsToPost_Should_ReturnStatusUnauthorized_When_UserNotAuthorized() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenThrow(UnauthorizedOperationException.class);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts/1/tags")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new TagCreateAndDeleteDTO("Valid,content"))))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void updatePost_Should_ReturnStatusOk_When_UserAuthorizedAndPostExists() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(createMockUser());

        Mockito.when(mockPostMapper.dtoToPost(Mockito.anyInt(), Mockito.any()))
                .thenReturn(createMockPost());

        Mockito.when(mockPostMapper.postToPostDisplayDTO(Mockito.any()))
                .thenReturn(new PostDisplayDTO());

        // Arrange, Act
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createMockPostCreateDTO())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    public void updatePost_Should_ReturnStatusBadRequest_When_InvalidBody() throws Exception {
        // Arrange, Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new PostCreateDTO())))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void updatePost_Should_ReturnStatusUnauthorized_When_UserNotAuthorized() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenThrow(UnauthorizedOperationException.class);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createMockPostCreateDTO())))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void updatePost_Should_ReturnStatusNotFound_When_PostDoesNotExist() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(createMockUser());

        Mockito.when(mockPostMapper.dtoToPost(Mockito.anyInt(), Mockito.any()))
                .thenThrow(EntityNotFoundException.class);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createMockPostCreateDTO())))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void likePost_Should_ReturnStatusOk_When_UserAuthorizedAndPostExists() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(createMockUser());

        Mockito.when(mockPostService.getByIdWithCommentsAndLikesAndTags(Mockito.anyInt()))
                .thenReturn(createMockPost());

        Mockito.when(mockPostMapper.postToPostDisplayDTO(Mockito.any()))
                .thenReturn(new PostDisplayDTO());

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/1/like"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    public void likePost_Should_ReturnStatusUnauthorized_When_UserNotAuthorized() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenThrow(UnauthorizedOperationException.class);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/1/like"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void likePost_Should_ReturnStatusNotFound_When_PostNotFound() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(createMockUser());

        Mockito.doThrow(EntityNotFoundException.class)
                .when(mockLikeService).likePost(Mockito.anyInt(), Mockito.any());

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/1/like"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void dislikePost_Should_ReturnStatusOk_When_UserAuthorizedAndPostExists() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(createMockUser());

        Mockito.when(mockPostService.getByIdWithCommentsAndLikesAndTags(Mockito.anyInt()))
                .thenReturn(createMockPost());

        Mockito.when(mockPostMapper.postToPostDisplayDTO(Mockito.any()))
                .thenReturn(new PostDisplayDTO());

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/1/dislike"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    public void dislikePost_Should_ReturnStatusUnauthorized_When_UserNotAuthorized() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenThrow(UnauthorizedOperationException.class);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/1/dislike"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void dislikePost_Should_ReturnStatusNotFound_When_PostNotFound() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(createMockUser());

        Mockito.doThrow(EntityNotFoundException.class)
                .when(mockLikeService).dislikePost(Mockito.anyInt(), Mockito.any());

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/1/dislike"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void updateTagsToPost_Should_ReturnStatusOk_When_EverythingIsValid() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(createMockUser());

        Mockito.when(mockPostService.getByIdWithCommentsAndLikesAndTags(Mockito.anyInt()))
                .thenReturn(createMockPost());

        Mockito.when(mockPostMapper.postToPostDisplayDTO(Mockito.any()))
                .thenReturn(new PostDisplayDTO());

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/1/tags")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new TagUpdateDTO("tag,two", "tag,three"))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    public void updateTagsToPost_Should_ReturnStatusBadRequest_When_InvalidBody() throws Exception {
        // Arrange, Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/1/tags")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new TagUpdateDTO())))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void updateTagsToPost_Should_ReturnStatusNotFound_When_PostNotFound() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(createMockUser());

        Mockito.doThrow(EntityNotFoundException.class).when(mockTagService)
                .updateTagFromPost(Mockito.anyInt(), Mockito.any(), Mockito.any(), Mockito.any());

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/1/tags")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new TagUpdateDTO("tag,two", "tag,three"))))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void updateTagsToPost_Should_ReturnStatusUnauthorized_When_UserNotAuthorized() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenThrow(UnauthorizedOperationException.class);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/1/tags")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new TagUpdateDTO("asd,asd", "ne,ne"))))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void updateComment_Should_ReturnStatusOk_When_EverythingIsValid() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(createMockUser());

        Mockito.when(mockPostMapper.dtoToComment(Mockito.any()))
                .thenReturn(createMockComment());

        Mockito.when(mockPostService.getByIdWithCommentsAndLikesAndTags(Mockito.anyInt()))
                .thenReturn(createMockPost());

        Mockito.when(mockPostMapper.postToPostDisplayDTO(Mockito.any()))
                .thenReturn(new PostDisplayDTO());

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/1/comments/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new CommentCreateDTO("valid content"))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    public void updateComment_Should_ReturnStatusBadRequest_When_InvalidBody() throws Exception {
        // Arrange, Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/1/comments/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new CommentCreateDTO())))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    public void updateComment_Should_ReturnStatusUnauthorized_When_UserNotAuthorized() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenThrow(UnauthorizedOperationException.class);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/1/comments/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new CommentCreateDTO("valid content"))))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void updateComment_Should_ReturnStatusNotFound_When_CommentOrPostDoesNotExist() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(createMockUser());

        Mockito.when(mockPostMapper.dtoToComment(Mockito.anyInt(), Mockito.any(), Mockito.anyInt()))
                .thenThrow(EntityNotFoundException.class);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/1/comments/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new CommentCreateDTO("valid content"))))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deletePost_Should_ReturnStatusOk_When_EverythingIsValid() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(createMockUser());

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void deletePost_Should_ReturnStatusUnauthorized_When_UserNotAuthorized() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenThrow(UnauthorizedOperationException.class);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/1"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void deletePost_Should_ReturnStatusNotFound_When_PostDoesNotExist() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(createMockUser());

        Mockito.doThrow(EntityNotFoundException.class).when(mockPostService)
                .deletePost(Mockito.anyInt(), Mockito.any());

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void removeTagsFromPost_Should_ReturnStatusOk_When_EverythingIsValid() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(createMockUser());

        Mockito.when(mockPostService.getByIdWithCommentsAndLikesAndTags(Mockito.anyInt()))
                .thenReturn(createMockPost());

        Mockito.when(mockPostMapper.postToPostDisplayDTO(Mockito.any()))
                .thenReturn(new PostDisplayDTO());

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/1/tags")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new TagCreateAndDeleteDTO("tag,two"))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    public void removeTagsFromPost_Should_ReturnStatusBadRequest_When_InvalidBody() throws Exception {
        // Arrange, Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/1/tags")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new TagCreateAndDeleteDTO())))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void removeTagsFromPost_Should_ReturnStatusUnauthorized_When_UserNotAuthorized() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenThrow(UnauthorizedOperationException.class);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/1/tags")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new TagCreateAndDeleteDTO("Valid,content"))))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void removeTagsFromPost_Should_ReturnStatusNotFound_When_PostDoesNotExist() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(createMockUser());

        Mockito.doThrow(EntityNotFoundException.class)
                .when(mockTagService).deleteTagFromPost(Mockito.anyInt(), Mockito.any(), Mockito.any());

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/1/tags")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new TagCreateAndDeleteDTO("valid,tag"))))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deleteComment_Should_ReturnStatusOk_When_EverythingIsValid() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(createMockUser());

        Mockito.when(mockPostService.getByIdWithCommentsAndLikesAndTags(Mockito.anyInt()))
                .thenReturn(createMockPost());

        Mockito.when(mockPostMapper.postToPostDisplayDTO(Mockito.any()))
                .thenReturn(new PostDisplayDTO());

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/1/comments/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    public void deleteComment_Should_ReturnStatusUnauthorized_When_UserNotAuthorized() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenThrow(UnauthorizedOperationException.class);

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/1/comments/1"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void deleteComment_Should_ReturnStatusNotFound_When_CommentOrPostNotFound() throws Exception {
        // Arrange
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any()))
                .thenReturn(createMockUser());

        Mockito.doThrow(EntityNotFoundException.class).when(mockCommentService)
                .deleteComment(Mockito.anyInt(), Mockito.anyInt(), Mockito.any());

        // Act, Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/1/comments/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}
