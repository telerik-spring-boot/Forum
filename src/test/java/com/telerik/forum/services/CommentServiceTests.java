package com.telerik.forum.services;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.post.Comment;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.user.User;
import com.telerik.forum.repositories.admin.AdminDetailsRepository;
import com.telerik.forum.repositories.comment.CommentRepository;
import com.telerik.forum.repositories.post.PostRepository;
import com.telerik.forum.services.comment.CommentServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static com.telerik.forum.DummyObjectProvider.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTests {

    @Mock
    private CommentRepository mockCommentRepository;

    @Mock
    private AdminDetailsRepository mockAdminDetailsRepository;

    @Mock
    private PostRepository mockPostRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    public void getComment_Should_ReturnComment_When_CommentExists(){
        // Arrange
        Mockito.when(mockCommentRepository.getById(Mockito.anyInt()))
                .thenReturn(createMockComment());

        // Act, Assert
        Assertions.assertNotNull(commentService.getComment(1));

        Mockito.verify(mockCommentRepository, Mockito.times(1))
                .getById(Mockito.anyInt());
    }

    @Test
    public void getComment_Should_Throw_When_CommentDoesNotExist(){
        // Arrange
        Mockito.when(mockCommentRepository.getById(Mockito.anyInt()))
                .thenReturn(null);

        // Act, Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> commentService.getComment(1));

        Mockito.verify(mockCommentRepository, Mockito.times(1))
                .getById(Mockito.anyInt());
    }

    @Test
    public void addComment_Should_AddComment_When_PostExists(){
        // Arrange
        Post post = createMockPost();
        Comment comment = createMockComment();
        User user = createMockUser();

        Mockito.when(mockPostRepository.getPostWithCommentsById(Mockito.anyInt()))
                .thenReturn(post);

        // Act
        commentService.addComment(post.getId(), comment, user);

        // Assert
        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getPostWithCommentsById(Mockito.anyInt());

        Mockito.verify(mockCommentRepository, Mockito.times(1))
                .create(comment);

        Assertions.assertAll(
                () -> Assertions.assertEquals(user, comment.getUser()),
                () -> Assertions.assertEquals(post, comment.getPost())
        );

    }

    @Test
    public void addComment_Should_Throw_When_PostDoesNotExist(){
        // Arrange
        Post post = createMockPost();
        Comment comment = createMockComment();
        User user = createMockUser();

        Mockito.when(mockPostRepository.getPostWithCommentsById(Mockito.anyInt()))
                .thenReturn(null);

        // Act, Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> commentService.addComment(post.getId(), comment, user));

        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getPostWithCommentsById(Mockito.anyInt());

    }

    @Test
    public void updateComment_Should_UpdateComment_When_CommentExistsAndUserIsCreatorAndNotBlocked(){
        // Arrange
        User user = createMockUser();
        Comment comment = createMockComment();
        Post post = createMockPost();

        comment.setUser(user);
        comment.setPost(post);

        user.setComments(List.of(comment));
        post.setComments(Set.of(comment));

        Mockito.when(mockCommentRepository.getById(comment.getId()))
                .thenReturn(comment);

        // Act, Assert
        Assertions.assertDoesNotThrow(() -> commentService.updateComment(comment, user));

        Mockito.verify(mockCommentRepository, Mockito.times(1))
                .update(comment);

    }

    @Test
    public void updateComment_Should_Throw_When_CommentExistsAndUserIsNotCreator(){
        // Arrange
        User user = createMockUser();
        Comment comment = createMockComment();
        Post post = createMockPost();
        user.setId(2);

        comment.setUser(createMockUser());
        comment.setPost(post);
        post.setComments(Set.of(comment));

        Mockito.when(mockCommentRepository.getById(comment.getId()))
                .thenReturn(comment);

        // Act, Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> commentService.updateComment(comment, user));

        Mockito.verify(mockCommentRepository, Mockito.times(0))
                .update(comment);

    }

    @Test
    public void updateComment_Should_Throw_When_CommentExistsAndUserIsBlocked(){
        // Arrange
        User user = createMockUser();
        Comment comment = createMockComment();
        Post post = createMockPost();
        user.setBlocked(true);

        comment.setUser(user);
        comment.setPost(post);

        user.setComments(List.of(comment));
        post.setComments(Set.of(comment));

        Mockito.when(mockCommentRepository.getById(comment.getId()))
                .thenReturn(comment);

        // Act, Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> commentService.updateComment(comment, user));

        Mockito.verify(mockCommentRepository, Mockito.times(0))
                .update(comment);

    }

    @Test
    public void deleteComment_Should_DeleteComment_When_EverythingIsValid(){
        // Arrange
        User user = createMockUser();
        Comment comment = createMockComment();
        Post post = createMockPost();
        user.setId(2);

        comment.setUser(createMockUser());
        comment.setPost(post);

        user.setComments(List.of(comment));
        post.setComments(Set.of(comment));

        Mockito.when(mockCommentRepository.getById(comment.getId()))
                .thenReturn(comment);

        Mockito.when(mockPostRepository.getPostWithCommentsById(Mockito.anyInt()))
                .thenReturn(post);

        Mockito.when(mockAdminDetailsRepository.getByUserId(Mockito.anyInt()))
                .thenReturn(createMockAdminDetails());

        // Act
        commentService.deleteComment(post.getId(), 1, user);

        // Assert
        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getPostWithCommentsById(Mockito.anyInt());

        Mockito.verify(mockAdminDetailsRepository, Mockito.times(1))
                .getByUserId(Mockito.anyInt());

        Mockito.verify(mockCommentRepository, Mockito.times(1))
                .delete(1);

    }

    @Test
    public void deleteComment_Should_Throw_When_PostDoesNotExist(){
        // Arrange

        Mockito.when(mockPostRepository.getPostWithCommentsById(Mockito.anyInt()))
                .thenReturn(null);

        // Act, Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> commentService.deleteComment(1, 1, createMockUser()));

        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getPostWithCommentsById(Mockito.anyInt());

    }

    @Test
    public void deleteComment_Should_Throw_When_CommentNumberOutOfBounds(){
        // Arrange
        Post post = createMockPost();

        Mockito.when(mockPostRepository.getPostWithCommentsById(Mockito.anyInt()))
                .thenReturn(post);

        // Act, Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> commentService.deleteComment(post.getId(), 2, createMockUser()));

        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getPostWithCommentsById(Mockito.anyInt());

    }

    @Test
    public void deleteComment_Should_Throw_When_UserIsNotOwnerOfCommentAndNotAdmin(){
        // Arrange
        User user = createMockUser();
        Comment comment = createMockComment();
        Post post = createMockPost();
        user.setId(2);

        comment.setUser(createMockUser());
        comment.setPost(post);

        user.setComments(List.of(comment));
        post.setComments(Set.of(comment));

        Mockito.when(mockCommentRepository.getById(comment.getId()))
                .thenReturn(comment);

        Mockito.when(mockPostRepository.getPostWithCommentsById(Mockito.anyInt()))
                .thenReturn(post);

        Mockito.when(mockAdminDetailsRepository.getByUserId(Mockito.anyInt()))
                .thenReturn(null);

        // Act, Assert

        Assertions.assertThrows(UnauthorizedOperationException.class, () -> commentService.deleteComment(post.getId(), 1, user));

        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getPostWithCommentsById(Mockito.anyInt());

        Mockito.verify(mockAdminDetailsRepository, Mockito.times(1))
                .getByUserId(Mockito.anyInt());

        Mockito.verify(mockCommentRepository, Mockito.times(0))
                .delete(1);

    }

    @Test
    public void deleteComment_Should_Throw_When_UserIsBlocked(){
        // Arrange
        User user = createMockUser();
        Comment comment = createMockComment();
        Post post = createMockPost();
        user.setBlocked(true);

        comment.setUser(user);
        comment.setPost(post);

        user.setComments(List.of(comment));
        post.setComments(Set.of(comment));

        Mockito.when(mockCommentRepository.getById(comment.getId()))
                .thenReturn(comment);

        Mockito.when(mockPostRepository.getPostWithCommentsById(Mockito.anyInt()))
                .thenReturn(post);

        Mockito.when(mockAdminDetailsRepository.getByUserId(Mockito.anyInt()))
                .thenReturn(null);


        // Act, Assert

        Assertions.assertThrows(UnauthorizedOperationException.class, () -> commentService.deleteComment(post.getId(), 1, user));

        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getPostWithCommentsById(Mockito.anyInt());

        Mockito.verify(mockAdminDetailsRepository, Mockito.times(1))
                .getByUserId(Mockito.anyInt());

        Mockito.verify(mockCommentRepository, Mockito.times(0))
                .delete(1);

    }


}
