package com.telerik.forum.services;

import com.telerik.forum.exceptions.AdminRoleManagementException;
import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.InvalidSortParameterException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.AdminDetails;
import com.telerik.forum.models.User;
import com.telerik.forum.models.filters.FilterUserOptions;
import com.telerik.forum.repositories.AdminDetailsRepository;
import com.telerik.forum.repositories.RoleRepository;
import com.telerik.forum.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {


    private static final String PERMISSION_ERROR_MESSAGE = "You do not have permission to perform this action";
    private static final String ALREADY_ADMIN_ERROR_MESSAGE = "User is already an admin.";
    private static final String NOT_ADMIN_ERROR_MESSAGE = "User is not an admin.";
    private final UserRepository userRepository;
    private final AdminDetailsRepository adminDetailsRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public AdminServiceImpl(UserRepository userRepository, AdminDetailsRepository adminDetailsRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.adminDetailsRepository = adminDetailsRepository;
        this.roleRepository = roleRepository;
    }


    @Override
    public List<AdminDetails> getAll() {
        return adminDetailsRepository.getAll();
    }

    @Override
    public List<User> getAllUsers(FilterUserOptions options, int requestUserId){
        authorization(requestUserId);

        options.getSortBy().ifPresent(this::validateSortField);

        options.getSortOrder().ifPresent(this::validateSortOrderField);

        return userRepository.getAll(options);
    }

    @Override
    public AdminDetails getByUserId(int id) {
        AdminDetails adminDetails = adminDetailsRepository.getByUserId(id);

        if(adminDetails == null){
            throw new EntityNotFoundException("Admin","user.id", id);
        }

        return adminDetails;
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
    @Transactional
    public void revokeAdminRights(int userId, int requestUserId) {
        authorization(requestUserId);

        User user = userRepository.getByIdWithRoles(userId);

        if(user == null){
            throw new EntityNotFoundException("User","user.id", userId);
        }

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("admin"));

        if(!isAdmin){
            throw new AdminRoleManagementException(NOT_ADMIN_ERROR_MESSAGE);
        }

        user.removeRole(roleRepository.findByName("ADMIN"));

        AdminDetails adminDetails = adminDetailsRepository.getByUserId(userId);

        userRepository.update(user);

        if(adminDetails != null && adminDetails.getPhoneNumber() != null ){
            adminDetailsRepository.delete(userId);
        }

    }

    @Override
    @Transactional
    public void giveAdminRights(int userId, String phoneNumber, int requestUserId) {
        authorization(requestUserId);

        User user = userRepository.getByIdWithRoles(userId);

        if(user == null){
            throw new EntityNotFoundException("User","user.id", userId);
        }

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("admin"));

        if(isAdmin){
            throw new AdminRoleManagementException(ALREADY_ADMIN_ERROR_MESSAGE);
        }

        user.addRole(roleRepository.findByName("ADMIN"));

        userRepository.update(user);

        if(phoneNumber != null){
            AdminDetails adminDetails = new AdminDetails(user, phoneNumber);

            adminDetailsRepository.create(adminDetails);
        }
    }

    @Override
    public void update(AdminDetails admin, int requestUserId) {
        authorization(requestUserId);

        AdminDetails databaseAdminDetails = adminDetailsRepository.getByUserId(admin.getUser().getId());

        if(databaseAdminDetails == null) {
            throw new EntityNotFoundException("Admin", "id", admin.getUser().getId());
        }

        if(admin.getPhoneNumber() == null && databaseAdminDetails.getPhoneNumber() != null){
            adminDetailsRepository.delete(admin.getUser().getId());
        }else if(databaseAdminDetails.getPhoneNumber() != null){
            adminDetailsRepository.update(admin);
        }else {
            adminDetailsRepository.create(admin);
        }
    }

    private void authorization(int id){
        User user = userRepository.getByIdWithRoles(id);

        if(user.isBlocked() || user.getRoles().stream()
                .noneMatch(role -> role.getName()
                        .equalsIgnoreCase("admin"))){
            throw new UnauthorizedOperationException(PERMISSION_ERROR_MESSAGE);

        }
    }

    private void validateSortField(String type){
        if(!type.equals("firstName") && !type.equals("lastName") && !type.equals("username")){
            throw new InvalidSortParameterException(type);
        }
    }

    private void validateSortOrderField(String type){
        if(!type.equalsIgnoreCase("asc") && !type.equalsIgnoreCase("desc")){
            throw new InvalidSortParameterException(type);
        }
    }

}
