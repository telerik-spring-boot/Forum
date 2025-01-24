package com.telerik.forum.services;

import com.telerik.forum.exceptions.DuplicateEntityException;
import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.Post;
import com.telerik.forum.models.User;
import com.telerik.forum.repositories.AdminRepository;
import com.telerik.forum.repositories.CommentRepository;
import com.telerik.forum.repositories.PostRepository;
import com.telerik.forum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }


    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public User getById(int id) {
        User user = userRepository.getById(id);

        if (user == null) {
            throw new EntityNotFoundException("User", "id", id);
        }

        return user;
    }

    @Override
    public User getByIdWithPosts(int id) {
        User user = userRepository.getByIdWithPosts(id);

        if (user == null) {
            throw new EntityNotFoundException("User", "id", id);
        }

        return user;
    }

    @Override
    public User getByIdWithComments(int id){
        User user = userRepository.getByIdWithComments(id);

        if (user == null) {
            throw new EntityNotFoundException("User", "id", id);
        }

        return user;
    }

    @Override
    public User getByEmail(String email) {
        User user = userRepository.getByEmail(email);

        if (user == null) {
            throw new EntityNotFoundException("User", "email", email);
        }

        return user;
    }

    @Override
    public User getByUsername(String username) {
        User user = userRepository.getByUsername(username);

        if (user == null) {
            throw new EntityNotFoundException("User", "username", username);
        }

        return user;
    }

    @Override
    public User getByFirstName(String firstName) {
        User user = userRepository.getByFirstName(firstName);

        if (user == null) {
            throw new EntityNotFoundException("User", "firstName", firstName);
        }

        return user;
    }

    @Override
    public void create(User userInput, User requestUser) {
        authorization(requestUser);

        boolean userAlreadyExists = userRepository.getByEmail(userInput.getEmailAddress()) != null;

        if (userAlreadyExists) {
            throw new DuplicateEntityException("User", "email", userInput.getEmailAddress());
        }

        userRepository.create(userInput);
    }

    @Override
    public void update(User userInput, User requestUser) {
        authorization(userInput, requestUser);

        User userToUpdate = userRepository.getByEmail(userInput.getEmailAddress());

        if (userToUpdate != null && userToUpdate.getId() != userInput.getId()) {
            throw new DuplicateEntityException("User", "email", userInput.getEmailAddress());
        }

        if (userToUpdate != null && !userToUpdate.getUsername().equals(userInput.getUsername())) {
            throw new UnauthorizedOperationException("You do not have permission to perform this action");
        }

        userRepository.update(userInput);
    }

    @Override
    public void delete(int id, User requestUser) {
        authorization(userRepository.getById(id), requestUser);

        userRepository.delete(id);
    }

    private void authorization(User user) {
        boolean isAdmin = adminRepository.getByUserId(user.getId()) != null;

        if (user.isBlocked() || !isAdmin) {
            throw new UnauthorizedOperationException("You do not have permission to perform this action");
        }
    }

    private void authorization(User userInput, User requestUser) {
        boolean isAdmin = adminRepository.getByUserId(requestUser.getId()) != null;

        if (requestUser.isBlocked() ||
                (!isAdmin && requestUser.getId() != userInput.getId())) {
            throw new UnauthorizedOperationException("You do not have permission to perform this action");
        }
    }
}
