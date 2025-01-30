package com.telerik.forum.services;

import com.telerik.forum.models.user.User;
import com.telerik.forum.repositories.comment.CommentRepository;
import com.telerik.forum.repositories.post.PostRepository;
import com.telerik.forum.repositories.role.RoleRepository;
import com.telerik.forum.repositories.user.UserRepository;
import com.telerik.forum.services.user.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.telerik.forum.DummyObjectProvider.createMockUser;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {


    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private PostRepository mockPostRepository;

    @Mock
    private RoleRepository mockRoleRepository;

    @Mock
    private CommentRepository mockCommentRepository;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    public void getById_Should_ReturnUser_When_IdIsValid(){
        // Arrange
        User user = createMockUser();

        Mockito.when(mockUserRepository.getById(Mockito.anyInt()))
                .thenReturn(user);

        // Act
        User returnedUser = userService.getById(1);

        // Assert
        Assertions.assertEquals(user, returnedUser);
        Mockito.verify(mockUserRepository, Mockito.times(1))
                .getById(Mockito.anyInt());
    }

}
