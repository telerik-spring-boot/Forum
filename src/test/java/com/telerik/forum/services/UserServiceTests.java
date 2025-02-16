package com.telerik.forum.services;

import com.telerik.forum.exceptions.DuplicateEntityException;
import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.user.AdminDetails;
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


import static com.telerik.forum.DummyObjectProvider.createMockAdminDetails;
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
    public void getById_Should_ReturnUser_When_IdIsValid() {
        // Arrange
        User user = createMockUser();
        user.setId(2);

        AdminDetails adminDetails = createMockAdminDetails();


        Mockito.when(mockUserRepository.getById(Mockito.anyInt()))
                .thenReturn(user);

        // Act
        User returnedUser = userService.getById(1, adminDetails.getUser());

        // Assert
        Assertions.assertEquals(user, returnedUser);
        Mockito.verify(mockUserRepository, Mockito.times(1))
                .getById(Mockito.anyInt());
    }

    @Test
    public void getById_Should_ThrowError_When_UserRequestIsBlocked() {
        // Arrange
        User userRequest = createMockUser();
        userRequest.setBlocked(true);

        //Act, Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> userService.getById(1, userRequest));
        Mockito.verify(mockUserRepository, Mockito.times(0))
                .getById(Mockito.anyInt());
    }

    @Test
    public void getById_Should_ThrowError_When_UserIdInvalid() {
        // Arrange
        Mockito.when(mockUserRepository.getById(Mockito.anyInt()))
                .thenReturn(null);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.getById(1, createMockUser()));
        Mockito.verify(mockUserRepository, Mockito.times(1))
                .getById(Mockito.anyInt());
    }

    @Test
    public void getByUsername_Should_ReturnUser_When_UsernameIsValid() {
        // Arrange
        User user = createMockUser();
        user.setUsername("test");

        Mockito.when(mockUserRepository.getByUsername(Mockito.anyString()))
                .thenReturn(user);

        // Act
        User returnedUser = userService.getByUsername(user.getUsername());

        // Assert
        Assertions.assertEquals(user, returnedUser);
        Mockito.verify(mockUserRepository, Mockito.times(1))
                .getByUsername(Mockito.anyString());
    }

    @Test
    public void getByUsername_Should_ThrowError_When_UsernameInvalid() {
        // Arrange
        Mockito.when(mockUserRepository.getByUsername(Mockito.anyString()))
                .thenReturn(null);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.getByUsername(Mockito.anyString()));
        Mockito.verify(mockUserRepository, Mockito.times(1))
                .getByUsername(Mockito.anyString());
    }

    @Test
    public void getByUsernameWithRoles_Should_ReturnUserWithRoles_When_UsernameIsValid() {
        // Arrange
        User user = createMockUser();
        AdminDetails adminDetails = createMockAdminDetails();
        user.setRoles(adminDetails.getUser().getRoles());

        Mockito.when(mockUserRepository.getByUsernameWithRoles(Mockito.anyString()))
                .thenReturn(user);

        // Act
        User returnedUser = userService.getByUsernameWithRoles(user.getUsername());

        // Assert
        Assertions.assertEquals(user, returnedUser);
        Mockito.verify(mockUserRepository, Mockito.times(1))
                .getByUsernameWithRoles(Mockito.anyString());
    }

    @Test
    public void getByUsernameWithRoles_Should_ThrowError_When_UsernameInvalid() {
        // Arrange
        Mockito.when(mockUserRepository.getByUsernameWithRoles(Mockito.anyString()))
                .thenReturn(null);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.getByUsernameWithRoles(Mockito.anyString()));
        Mockito.verify(mockUserRepository, Mockito.times(1))
                .getByUsernameWithRoles(Mockito.anyString());
    }

    @Test
    public void create_Should_CreateUser_When_UsernameAndEmailAreValid() {
        // Arrange
        User user = createMockUser();

        Mockito.doNothing().when(mockUserRepository).create(user);

        // Act
        userService.create(user);

        // Assert
        Mockito.verify(mockUserRepository, Mockito.times(1))
                .create(user);
    }

    @Test
    public void create_Should_Throw_When_UsernameIsInvalid() {
        // Arrange
        User user = createMockUser();

        Mockito.when(mockUserRepository.getByUsername(Mockito.anyString()))
                .thenReturn(user);

        // Assert
        Assertions.assertThrows(DuplicateEntityException.class, () -> userService.create(user));
        Mockito.verify(mockUserRepository, Mockito.times(0))
                .create(user);
    }

    @Test
    public void create_Should_Throw_When_EmailIsInvalid() {
        // Arrange
        User user = createMockUser();

        Mockito.when(mockUserRepository.getByEmail(Mockito.anyString()))
                .thenReturn(user);

        // Assert
        Assertions.assertThrows(DuplicateEntityException.class, () -> userService.create(user));
        Mockito.verify(mockUserRepository, Mockito.times(0))
                .create(user);
    }

    @Test
    public void delete_Should_Throw_When_UserRequestIsBlocked() {
        // Arrange
        User user = createMockUser();
        user.setId(1);
        user.setBlocked(true);

        // Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> userService.delete(1, user));
        Mockito.verify(mockUserRepository, Mockito.times(0))
                .delete(Mockito.anyInt());
    }

    @Test
    public void delete_Should_Throw_When_UserRequestIsNotAdminOrUser() {
        // Arrange
        User userToDelete = createMockUser();
        userToDelete.setId(1);

        User userRequest = createMockUser();
        userRequest.setId(2);

        // Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> userService.delete(1, userRequest));
        Mockito.verify(mockUserRepository, Mockito.times(0))
                .delete(Mockito.anyInt());
    }

    @Test
    public void delete_Should_Delete_When_UserRequestIsAdmin() {
        // Arrange
        User userToDelete = createMockUser();
        userToDelete.setId(1);

        User userRequest = createMockUser();
        userRequest.setId(2);
        AdminDetails adminDetails = createMockAdminDetails();
        userRequest.setRoles(adminDetails.getUser().getRoles());

        Mockito.doNothing().when(mockUserRepository).delete(1);
        Mockito.when(mockUserRepository.getById(Mockito.anyInt()))
                .thenReturn(userToDelete);

        // Act
        userService.delete(1, userRequest);

        // Assert
        Mockito.verify(mockUserRepository, Mockito.times(1))
                .delete(Mockito.anyInt());
    }

    @Test
    public void delete_Should_Delete_When_UserRequestIsDeletedUser() {
        // Arrange
        User userToDelete = createMockUser();
        userToDelete.setId(1);

        Mockito.doNothing().when(mockUserRepository).delete(1);
        Mockito.when(mockUserRepository.getById(Mockito.anyInt()))
                .thenReturn(userToDelete);

        // Act
        userService.delete(1, userToDelete);

        // Assert
        Mockito.verify(mockUserRepository, Mockito.times(1))
                .delete(Mockito.anyInt());
    }

    @Test
    public void delete_Should_Throw_When_UserIdInvalid() {
        // Arrange
        User userToDelete = createMockUser();
        AdminDetails adminDetails = createMockAdminDetails();
        userToDelete.setRoles(adminDetails.getUser().getRoles());
        userToDelete.setId(1);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.delete(5, userToDelete));
        Mockito.verify(mockUserRepository, Mockito.times(0))
                .delete(Mockito.anyInt());
    }

}
