package com.telerik.forum.services;

import com.telerik.forum.models.User;
import com.telerik.forum.repositories.CommentRepository;
import com.telerik.forum.repositories.PostRepository;
import com.telerik.forum.repositories.RoleRepository;
import com.telerik.forum.repositories.UserRepository;
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
