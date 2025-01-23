package com.telerik.forum.services;

import com.telerik.forum.exceptions.DuplicateEntityException;
import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.Admin;
import com.telerik.forum.models.User;
import com.telerik.forum.repositories.AdminRepository;
import com.telerik.forum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public AdminServiceImpl(UserRepository userRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }


    @Override
    public List<Admin> getAll() {
        return adminRepository.getAll();
    }

    @Override
    public Admin getByUserId(int id) {
        Admin byUserId = adminRepository.getByUserId(id);

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

        if(adminRepository.getByUserId(user.getId()) != null) {
            throw new DuplicateEntityException("Admin", "id", user.getId());
        }

        Admin admin = new Admin();
        admin.setPhoneNumber(phoneNumber);
        admin.setUser(user);

        adminRepository.create(admin);
    }

    @Override
    public void update(Admin admin, int requestUserId) {
        authorization(requestUserId);

        if(adminRepository.getByUserId(admin.getUser().getId()) == null) {
            throw new EntityNotFoundException("Admin", "id", admin.getUser().getId());
        }

        adminRepository.update(admin);
    }

    @Override
    public void delete(int id, int requestUserId) {
        authorization(requestUserId);

        if(adminRepository.getByUserId(id) == null) {
            throw new EntityNotFoundException("Admin", "id", id);
        }

        adminRepository.delete(id);
    }

    public void authorization(int id){
        Admin admin = adminRepository.getByUserId(id);

        if(admin == null || admin.getUser().isBlocked()){
            throw new UnauthorizedOperationException("You do not have permission to perform this action");
        }
    }
}
