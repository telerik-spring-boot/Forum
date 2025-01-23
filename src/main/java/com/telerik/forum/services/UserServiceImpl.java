package com.telerik.forum.services;

import com.telerik.forum.exceptions.DuplicateEntityException;
import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.User;
import com.telerik.forum.repositories.AdminRepository;
import com.telerik.forum.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

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
        return userRepository.getById(id);
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.getByEmail(email);
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.getByUsername(username);
    }

    @Override
    public User getByFirstName(String firstName) {
        return userRepository.getByFirstName(firstName);
    }

    @Override
    public void create(User userInput, User requestUser) {
        authorization(requestUser);

        boolean userAlreadyExists = true;

        try {
            userRepository.getByEmail(userInput.getEmailAddress());
        } catch (EntityNotFoundException e) {
            userAlreadyExists = false;
        }

        if (userAlreadyExists) {
            throw new DuplicateEntityException("User", "email", userInput.getEmailAddress());
        }

        userRepository.create(userInput);
    }

    @Override
    public void update(User userInput, User requestUser) {
        authorization(userInput, requestUser);

        boolean userAlreadyExists = true;

        try {
            User userToUpdate = userRepository.getByEmail(userInput.getEmailAddress());

            if (userToUpdate.getId() == userInput.getId()) {
                userAlreadyExists = false;
            }

            if(!userToUpdate.getUsername().equals(userInput.getUsername())){
                throw new UnauthorizedOperationException("You do not have permission to perform this action");
            }

        } catch (EntityNotFoundException e) {
            userAlreadyExists = false;
        }

        if(userAlreadyExists) {
            throw new DuplicateEntityException("User", "email", userInput.getEmailAddress());
        }

        userRepository.update(userInput);
    }

    @Override
    public void delete(int id, User requestUser) {
        authorization(userRepository.getById(id), requestUser);

        userRepository.delete(id);
    }

    private void authorization(User user) {
        if(user.isBlocked() || !adminRepository.existsById(user.getId())) {
            throw new UnauthorizedOperationException("You do not have permission to perform this action");
        }
    }

    private void authorization(User userInput, User requestUser) {
        if(requestUser.isBlocked() ||
                (!adminRepository.existsById(requestUser.getId()) && requestUser.getId() != userInput.getId())) {
            throw new UnauthorizedOperationException("You do not have permission to perform this action");
        }
    }
}
