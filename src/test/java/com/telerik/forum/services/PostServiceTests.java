package com.telerik.forum.services;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.InvalidSortParameterException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.filters.FilterPostOptions;
import com.telerik.forum.models.post.Like;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.user.AdminDetails;
import com.telerik.forum.models.user.User;
import com.telerik.forum.repositories.admin.AdminDetailsRepository;
import com.telerik.forum.repositories.post.PostRepository;
import com.telerik.forum.services.post.PostServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.telerik.forum.DummyObjectProvider.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTests {

    @Mock
    private PostRepository mockPostRepository;

    @Mock
    private AdminDetailsRepository mockAdminRepository;

    @InjectMocks
    private PostServiceImpl postService;


    @Test
    public void getAllPosts_Should_ReturnAllPosts_When_ThereArePosts(){
        // Arrange
        Mockito.when(mockPostRepository.getAllPosts())
                .thenReturn(List.of(createMockPost()));

        // Act
        List<Post> posts = postService.getAllPosts();

        // Assert
        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getAllPosts();

        Assertions.assertEquals(1, posts.size());
    }

    @Test
    public void getAllPostsWithFilters_Should_ReturnAllPosts_When_NoFiltrationProvided(){
        // Arrange
        Mockito.when(mockPostRepository.getAllPostsWithFilters(Mockito.any()))
                .thenReturn(List.of(createMockPost()));

        // Act
        List<Post> posts = postService.getAllPostsWithFilters(new FilterPostOptions(null, null, null, null, null, null, null, null));

        // Assert
        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getAllPostsWithFilters(Mockito.any());

        Assertions.assertEquals(1, posts.size());
    }

    @Test
    public void getAllPostsWithFilters_Should_ReturnFilteredPosts_When_ValidParametersProvided(){
        // Arrange
        List<Post> expectedPosts = new ArrayList<>();
        Post post = createMockPost();

        post.setId(2);
        Like newLike = new Like();
        newLike.setReaction(6);
        post.setLikes(Set.of(newLike));

        expectedPosts.add(createMockPost());
        expectedPosts.add(post);

        Mockito.when(mockPostRepository.getAllPostsWithFilters(Mockito.any()))
                .thenReturn(expectedPosts);

        // Act
        List<Post> resultPosts = postService.getAllPostsWithFilters(new FilterPostOptions(null, null, null, null, 2L, 5L, null, null));

        // Assert
        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getAllPostsWithFilters(Mockito.any());

        Assertions.assertEquals(0, resultPosts.size());
    }

    @Test
    public void getAllPostsWithFilters_Should_ThrowException_When_InvalidSortFieldProvided(){
        // Arrange, Act, Assert
        Assertions.assertThrows(InvalidSortParameterException.class,
                () -> postService.getAllPostsWithFilters(new FilterPostOptions(null, null, null, null, null, null, "ffff", "ggggg")));

        Mockito.verify(mockPostRepository, Mockito.times(0))
                .getAllPostsWithFilters(Mockito.any());
    }

    @Test
    public void getMostCommentedPosts_Should_ReturnMostCommentedPosts(){
        // Arrange
        Mockito.when(mockPostRepository.getMostCommentedPosts(Mockito.anyInt()))
                .thenReturn(List.of(createMockPost()));

        // Act
        List<Post> posts = postService.getMostCommentedPosts(1);

        // Assert
        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getMostCommentedPosts(Mockito.anyInt());

        Assertions.assertEquals(1, posts.size());
    }

    @Test
    public void getMostLikedPosts_Should_ReturnMostLikedPosts(){
        // Arrange
        Mockito.when(mockPostRepository.getMostLikedPosts(Mockito.anyInt()))
                .thenReturn(List.of(createMockPost()));

        // Act
        List<Post> posts = postService.getMostLikedPosts(1);

        // Assert
        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getMostLikedPosts(Mockito.anyInt());

        Assertions.assertEquals(1, posts.size());
    }

    @Test
    public void getMostRecentPosts_Should_ReturnMostRecentPosts(){
        // Arrange
        Mockito.when(mockPostRepository.getMostRecentPosts(Mockito.anyInt()))
                .thenReturn(List.of(createMockPost()));

        // Act
        List<Post> posts = postService.getMostRecentPosts(1);

        // Assert
        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getMostRecentPosts(Mockito.anyInt());

        Assertions.assertEquals(1, posts.size());
    }

    @Test
    public void getById_Should_ReturnPost_When_PostWithIdExists(){
        // Arrange
        Mockito.when(mockPostRepository.getPostById(1))
                .thenReturn(createMockPost());

        // Act
        Post post = postService.getById(1);

        // Assert
        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getPostById(1);

        Assertions.assertNotNull(post);
    }

    @Test
    public void getById_Should_Throw_When_PostWithIdDoesNotExists(){
        // Arrange
        Mockito.when(mockPostRepository.getPostById(Mockito.anyInt()))
                .thenReturn(null);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> postService.getById(2));

        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getPostById(Mockito.anyInt());
    }

    @Test
    public void getByIdWithComments_Should_ReturnPostWithComments_When_PostWithIdExists(){
        // Arrange
        Post expectedPost = createMockPost();

        expectedPost.setComments(Set.of(createMockComment()));

        Mockito.when(mockPostRepository.getPostWithCommentsById(1))
                .thenReturn(expectedPost);

        // Act
        Post resultPost = postService.getByIdWithComments(1);

        // Assert
        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getPostWithCommentsById(1);

        Assertions.assertNotNull(resultPost.getComments());
    }

    @Test
    public void getByIdWithComments_Should_Throw_When_PostWithIdDoesNotExists(){
        // Arrange
        Mockito.when(mockPostRepository.getPostWithCommentsById(Mockito.anyInt()))
                .thenReturn(null);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> postService.getByIdWithComments(2));

        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getPostWithCommentsById(Mockito.anyInt());
    }

    @Test
    public void getByIdWithLikes_Should_ReturnPostWithLikes_When_PostWithIdExists(){
        // Arrange
        Post expectedPost = createMockPost();

        expectedPost.setLikes(Set.of(createMockLike()));

        Mockito.when(mockPostRepository.getPostWithLikesById(1))
                .thenReturn(expectedPost);

        // Act
        Post resultPost = postService.getByIdWithLikes(1);

        // Assert
        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getPostWithLikesById(1);

        Assertions.assertNotNull(resultPost.getLikes());
    }

    @Test
    public void getByIdWithLikes_Should_Throw_When_PostWithIdDoesNotExists(){
        // Arrange
        Mockito.when(mockPostRepository.getPostWithLikesById(Mockito.anyInt()))
                .thenReturn(null);


        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> postService.getByIdWithLikes(2));

        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getPostWithLikesById(Mockito.anyInt());
    }

    @Test
    public void getByIdWithCommentsAndLikesAndTags_Should_ReturnPostWithLikesAndComments_When_PostWithIdExists(){
        // Arrange
        Post expectedPost = createMockPost();

        expectedPost.setLikes(Set.of(createMockLike()));
        expectedPost.setComments(Set.of(createMockComment()));
        expectedPost.setTags(Set.of(createMockTag()));

        Mockito.when(mockPostRepository.getPostWithCommentsAndLikesAndTagsById(1))
                .thenReturn(expectedPost);

        // Act
        Post resultPost = postService.getByIdWithCommentsAndLikesAndTags(1);

        // Assert
        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getPostWithCommentsAndLikesAndTagsById(1);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(resultPost.getTags()),
                () -> Assertions.assertNotNull(resultPost.getLikes()),
                () -> Assertions.assertNotNull(resultPost.getComments()));
    }

    @Test
    public void getByIdWithCommentsAndLikesAndTags_Should_Throw_When_PostWithIdDoesNotExists(){
        // Arrange
        Mockito.when(mockPostRepository.getPostWithCommentsAndLikesAndTagsById(Mockito.anyInt()))
                .thenReturn(null);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> postService.getByIdWithCommentsAndLikesAndTags(2));

        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getPostWithCommentsAndLikesAndTagsById(Mockito.anyInt());
    }

    @Test
    public void createPost_Should_CreatePost_When_UserIsNotBlocked(){
        // Arrange
        User user = createMockUser();
        Post post = createMockPost();

        Mockito.doNothing().when(mockPostRepository).create(post);

        // Act
        postService.createPost(post, user);

        // Assert
        Mockito.verify(mockPostRepository, Mockito.times(1))
                .create(post);

        Assertions.assertEquals(user, post.getUser());
    }

    @Test
    public void createPost_Should_Throw_When_UserIsBlocked(){
        // Arrange
        Post post = createMockPost();
        User user = createMockUser();
        user.setBlocked(true);

        //Act, Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> postService.createPost(post, user));

        Mockito.verify(mockPostRepository, Mockito.times(0))
                .create(Mockito.any());
    }


    @Test
    public void updatePost_Should_UpdatePost_When_UserIsNotBlockedAndHasPermissions(){
        // Arrange
        User user = createMockUser();
        Post post = createMockPost();

        user.setPosts(List.of(post));
        post.setUser(user);

        Mockito.doNothing().when(mockPostRepository).update(post);

        Mockito.when(mockPostRepository.getPostById(1))
                .thenReturn(post);

        // Act
        postService.updatePost(post, user);

        // Assert
        Mockito.verify(mockPostRepository, Mockito.times(1))
                .update(post);
    }

    @Test
    public void updatePost_Should_Throw_When_UserIsBlocked(){
        // Arrange
        Post post = createMockPost();
        User user = createMockUser();

        post.setUser(user);
        user.setBlocked(true);

        Mockito.when(mockPostRepository.getPostById(1))
                .thenReturn(post);

        //Act, Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> postService.updatePost(post, user));

        Mockito.verify(mockPostRepository, Mockito.times(0))
                .update(Mockito.any());
    }

    @Test
    public void updatePost_Should_Throw_When_UserIsNotOwnerOfPost(){
        // Arrange
        Post post = createMockPost();
        User user = createMockUser();

        user.setId(2);
        post.setUser(user);
        user.setBlocked(true);

        Mockito.when(mockPostRepository.getPostById(1))
                .thenReturn(post);

        //Act, Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> postService.updatePost(post, createMockUser()));

        Mockito.verify(mockPostRepository, Mockito.times(0))
                .update(Mockito.any());
    }


    @Test
    public void deletePost_Should_DeletePostFromUser_When_UserIsNotBlockedAndHasPermissions(){
        // Arrange
        User user = createMockUser();
        Post post = createMockPost();

        user.setPosts(List.of(post));
        post.setUser(user);

        Mockito.doNothing().when(mockPostRepository).delete(post.getId());

        Mockito.when(mockPostRepository.getPostById(1))
                .thenReturn(post);

        Mockito.when(mockAdminRepository.getByUserId(1))
                .thenReturn(null);

        // Act
        postService.deletePost(post.getId(), user);

        // Assert
        Mockito.verify(mockPostRepository, Mockito.times(1))
                .delete(post.getId());

    }

    @Test
    public void deletePost_Should_Throw_When_UserIsBlocked(){
        // Arrange
        Post post = createMockPost();
        User user = createMockUser();

        post.setUser(user);
        user.setBlocked(true);

        Mockito.when(mockPostRepository.getPostById(1))
                .thenReturn(post);

        Mockito.when(mockAdminRepository.getByUserId(1))
                .thenReturn(null);

        //Act, Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> postService.deletePost(post.getId(), user));

        Mockito.verify(mockPostRepository, Mockito.times(0))
                .delete(Mockito.anyInt());
    }

    @Test
    public void deletePost_Should_Throw_When_UserIsNotOwnerOfPostAndNotAdmin(){
        // Arrange
        Post post = createMockPost();
        User user = createMockUser();

        user.setId(2);
        post.setUser(user);
        user.setBlocked(true);

        Mockito.when(mockPostRepository.getPostById(1))
                .thenReturn(post);

        Mockito.when(mockAdminRepository.getByUserId(1))
                .thenReturn(null);

        //Act, Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> postService.deletePost(post.getId(), createMockUser()));

        Mockito.verify(mockPostRepository, Mockito.times(0))
                .delete(Mockito.anyInt());
    }

    @Test
    public void deletePost_Should_DeletePostFromUser_When_UserIsNotOwnerOfPostButIsAdmin(){
        // Arrange
        Post post = createMockPost();
        User user = createMockUser();
        AdminDetails adminDetails = createMockAdminDetails();

        user.setId(2);
        post.setUser(user);

        Mockito.when(mockPostRepository.getPostById(1))
                .thenReturn(post);

        Mockito.when(mockAdminRepository.getByUserId(1))
                .thenReturn(adminDetails);

        // Act
        postService.deletePost(post.getId(), adminDetails.getUser());

        //Assert
        Mockito.verify(mockPostRepository, Mockito.times(1))
                .delete(Mockito.anyInt());
    }




}
