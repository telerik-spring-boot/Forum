package com.telerik.forum.services;

import com.telerik.forum.exceptions.DuplicateEntityException;
import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.Admin;
import com.telerik.forum.models.User;
import com.telerik.forum.repositories.AdminRepositoryOld;
import com.telerik.forum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceOldImpl implements AdminServiceOld {

    private final UserRepository userRepository;
    private final AdminRepositoryOld adminRepositoryOld;

    @Autowired
    public AdminServiceOldImpl(UserRepository userRepository, AdminRepositoryOld adminRepositoryOld) {
        this.userRepository = userRepository;
        this.adminRepositoryOld = adminRepositoryOld;
    }


    @Override
    public List<Admin> getAll() {
        return adminRepositoryOld.getAll();
    }

    @Override
    public Admin getByUserId(int id) {
        Admin byUserId = adminRepositoryOld.getByUserId(id);

        if (byUserId == null) {
            throw new EntityNotFoundException("Admin", "user.id", id);
        }

        return byUserId;
    }

    @Override
    public void blockUser(User userToBeBlocked, int requestUserId) {
        authorization(requestUserId);

        userToBeBlocked.setBlocked(true);
        userRepository.update(userToBeBlocked);
    }

    @Override
    public void unblockUser(User userToBeUnblocked, int requestUserId) {
        authorization(requestUserId);

        userToBeUnblocked.setBlocked(false);

        userRepository.update(userToBeUnblocked);
    }

    @Override
    public void create(User user, String phoneNumber, int requestUserId) {
        authorization(requestUserId);

        if(adminRepositoryOld.getByUserId(user.getId()) != null) {
            throw new DuplicateEntityException("Admin", "id", user.getId());
        }

        Admin admin = new Admin();
        admin.setPhoneNumber(phoneNumber);
        admin.setUser(user);

        adminRepositoryOld.create(admin);
    }

    @Override
    public void update(Admin admin, int requestUserId) {
        authorization(requestUserId);

        if(adminRepositoryOld.getByUserId(admin.getUser().getId()) == null) {
            throw new EntityNotFoundException("Admin", "id", admin.getUser().getId());
        }

        adminRepositoryOld.update(admin);
    }

    @Override
    public void delete(int id, int requestUserId) {
        authorization(requestUserId);

        if(adminRepositoryOld.getByUserId(id) == null) {
            throw new EntityNotFoundException("Admin", "id", id);
        }

        adminRepositoryOld.delete(id);
    }

    public void authorization(int id){
        Admin admin = adminRepositoryOld.getByUserId(id);

        if(admin == null || admin.getUser().isBlocked()){
            throw new UnauthorizedOperationException("You do not have permission to perform this action");
        }
    }
}
