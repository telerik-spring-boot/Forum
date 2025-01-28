package com.telerik.forum.services;


import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.post.Like;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.user.User;
import com.telerik.forum.repositories.like.LikeRepository;
import com.telerik.forum.repositories.post.PostRepository;
import com.telerik.forum.services.like.LikeServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static com.telerik.forum.DummyObjectProvider.*;
import static com.telerik.forum.DummyObjectProvider.createMockUser;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTests {

    @Mock
    private LikeRepository mockLikeRepository;

    @Mock
    private PostRepository mockPostService;

    @InjectMocks
    private LikeServiceImpl likeService;

    @Test
    public void likePost_Should_CreateLikeForPost_When_LikeDoesNotExistAndEverythingIsValid(){
        // Arrange
        User user = createMockUser();
        Post post = createMockPost();
        Like like = createMockLike();

        like.setUser(user);
        like.setPost(post);

        user.setId(2);

        post.setLikes(Set.of(like));

        Mockito.when(mockPostService.getPostWithLikesById(Mockito.anyInt()))
                .thenReturn(post);

        // Act
        likeService.likePost(post.getId(), createMockUser());

        //Assert

        Mockito.verify(mockPostService, Mockito.times(1))
                .getPostWithLikesById(Mockito.anyInt());

        Mockito.verify(mockLikeRepository, Mockito.times(1))
                .create(Mockito.any());

    }

    @Test
    public void likePost_Should_UpdateLikeReaction_When_LikeExistAndReactionDiffersFromInput(){
        // Arrange
        User user = createMockUser();
        User user2 = createMockUser();
        Post post = createMockPost();
        Like like = createMockLike();
        Like like2 = createMockLike();

        like.setUser(user);
        like.setPost(post);
        like.setReaction(-1);

        user2.setId(2);
        like2.setId(2);
        like2.setUser(user2);
        like2.setPost(post);

        post.setLikes(Set.of(like, like2));

        Mockito.when(mockPostService.getPostWithLikesById(Mockito.anyInt()))
                .thenReturn(post);

        // Act
        likeService.likePost(post.getId(), user);

        //Assert

        Mockito.verify(mockPostService, Mockito.times(1))
                .getPostWithLikesById(Mockito.anyInt());

        Mockito.verify(mockLikeRepository, Mockito.times(1))
                .update(like);

        Assertions.assertEquals(1, like.getReaction());

    }

    @Test
    public void likePost_Should_DeleteLike_When_LikeExistAndReactionIsTheSame(){
        // Arrange
        User user = createMockUser();
        Post post = createMockPost();
        Like like = createMockLike();

        like.setUser(user);
        like.setPost(post);

        post.setLikes(Set.of(like));

        Mockito.when(mockPostService.getPostWithLikesById(Mockito.anyInt()))
                .thenReturn(post);

        // Act
        likeService.likePost(post.getId(), user);

        //Assert

        Mockito.verify(mockPostService, Mockito.times(1))
                .getPostWithLikesById(Mockito.anyInt());

        Mockito.verify(mockLikeRepository, Mockito.times(1))
                .delete(like.getId());

    }

    @Test
    public void likePost_Should_Throw_When_PostDoesNotExist(){

        // Arrange
        User user = createMockUser();

        Mockito.when(mockPostService.getPostWithLikesById(Mockito.anyInt()))
                .thenReturn(null);

        // Act, Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> likeService.likePost(1, user));

        Mockito.verify(mockPostService, Mockito.times(1))
                .getPostWithLikesById(Mockito.anyInt());
    }

    @Test
    public void likePost_Should_Throw_When_UserBlocked(){
        // Arrange
        User user = createMockUser();
        user.setBlocked(true);

        // Act, Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> likeService.likePost(1, user));
    }


    @Test
    public void dislikePost_Should_CreateNegativeLikeForPost_When_LikeDoesNotExistAndEverythingIsValid(){
        // Arrange
        User user = createMockUser();
        Post post = createMockPost();
        Like like = createMockLike();

        like.setUser(user);
        like.setPost(post);

        user.setId(2);

        post.setLikes(Set.of(like));

        Mockito.when(mockPostService.getPostWithLikesById(Mockito.anyInt()))
                .thenReturn(post);

        // Act
        likeService.dislikePost(post.getId(), createMockUser());

        //Assert
        Mockito.verify(mockPostService, Mockito.times(1))
                .getPostWithLikesById(Mockito.anyInt());

        Mockito.verify(mockLikeRepository, Mockito.times(1))
                .create(Mockito.any());

    }

    @Test
    public void dislikePost_Should_UpdateLikeReaction_When_LikeExistAndReactionDiffersFromInput(){
        // Arrange
        User user = createMockUser();
        User user2 = createMockUser();
        Post post = createMockPost();
        Like like = createMockLike();
        Like like2 = createMockLike();

        like.setUser(user);
        like.setPost(post);

        user2.setId(2);
        like2.setId(2);
        like2.setUser(user2);
        like2.setPost(post);

        post.setLikes(Set.of(like, like2));

        Mockito.when(mockPostService.getPostWithLikesById(Mockito.anyInt()))
                .thenReturn(post);

        // Act
        likeService.dislikePost(post.getId(), user);

        //Assert
        Mockito.verify(mockPostService, Mockito.times(1))
                .getPostWithLikesById(Mockito.anyInt());

        Mockito.verify(mockLikeRepository, Mockito.times(1))
                .update(like);

        Assertions.assertEquals(-1, like.getReaction());

    }

    @Test
    public void dislikePost_Should_DeleteLike_When_LikeExistAndReactionIsTheSame(){
        // Arrange
        User user = createMockUser();
        Post post = createMockPost();
        Like like = createMockLike();

        like.setUser(user);
        like.setPost(post);
        like.setReaction(-1);

        post.setLikes(Set.of(like));

        Mockito.when(mockPostService.getPostWithLikesById(Mockito.anyInt()))
                .thenReturn(post);

        // Act
        likeService.dislikePost(post.getId(), user);

        //Assert
        Mockito.verify(mockPostService, Mockito.times(1))
                .getPostWithLikesById(Mockito.anyInt());

        Mockito.verify(mockLikeRepository, Mockito.times(1))
                .delete(like.getId());

    }

    @Test
    public void dislikePost_Should_Throw_When_PostDoesNotExist(){

        // Arrange
        User user = createMockUser();

        Mockito.when(mockPostService.getPostWithLikesById(Mockito.anyInt()))
                .thenReturn(null);

        // Act, Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> likeService.dislikePost(1, user));

        Mockito.verify(mockPostService, Mockito.times(1))
                .getPostWithLikesById(Mockito.anyInt());
    }

    @Test
    public void dislikePost_Should_Throw_When_UserBlocked(){
        // Arrange
        User user = createMockUser();
        user.setBlocked(true);

        // Act, Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> likeService.dislikePost(1, user));
    }

}
