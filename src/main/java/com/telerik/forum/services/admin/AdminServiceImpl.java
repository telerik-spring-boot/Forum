package com.telerik.forum.services.admin;

import com.telerik.forum.exceptions.AdminRoleManagementException;
import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.dtos.userDTOs.UserDisplayMvcDTO;
import com.telerik.forum.models.user.AdminDetails;
import com.telerik.forum.models.user.User;
import com.telerik.forum.models.filters.FilterUserOptions;
import com.telerik.forum.repositories.admin.AdminDetailsRepository;
import com.telerik.forum.repositories.role.RoleRepository;
import com.telerik.forum.repositories.user.UserRepository;
import com.telerik.forum.repositories.utilities.SortingHelper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public List<AdminDetails> getAll(User userRequest) {
        return adminDetailsRepository.getAll();
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAll();
    }

    @Override
    public List<UserDisplayMvcDTO> getAllUsersMvc() {
        return userRepository.getAllMvc();
    }

    @Override
    public Page<User> getAllUsers(FilterUserOptions options, User userRequest, Pageable pageable) {
        authorization(userRequest);

        options.getSortBy().ifPresent(SortingHelper::validateSortByFieldUser);

        options.getSortOrder().ifPresent(SortingHelper::validateSortOrderField);

        return userRepository.getAll(options, pageable);
    }

    @Override
    public AdminDetails getByUserId(int id, User userRequest) {
        AdminDetails adminDetails = adminDetailsRepository.getByUserId(id);

        if (adminDetails == null) {
            throw new EntityNotFoundException("Admin", "user.id", id);
        }

        return adminDetails;
    }

    @Override
    public void blockUser(User userToBeBlocked, User userRequest) {
        authorization(userRequest);

        userToBeBlocked.setBlocked(true);

        userRepository.update(userToBeBlocked);
    }

    @Override
    public void unblockUser(User userToBeUnblocked, User userRequest) {
        authorization(userRequest);

        userToBeUnblocked.setBlocked(false);

        userRepository.update(userToBeUnblocked);
    }

    @Override
    @Transactional
    public void revokeAdminRights(int userId, User userRequest) {
        authorization(userRequest);

        User user = userRepository.getByIdWithRoles(userId);

        if (user == null) {
            throw new EntityNotFoundException("User", "user.id", userId);
        }

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("admin"));

        if (!isAdmin) {
            throw new AdminRoleManagementException(NOT_ADMIN_ERROR_MESSAGE);
        }

        user.removeRole(roleRepository.findByName("ADMIN"));

        AdminDetails adminDetails = adminDetailsRepository.getByUserId(userId);

        userRepository.update(user);

        if (adminDetails != null && adminDetails.getPhoneNumber() != null) {
            adminDetailsRepository.delete(userId);
        }

    }

    @Override
    @Transactional
    public void giveAdminRights(int userId, String phoneNumber, User userRequest) {
        authorization(userRequest);

        User user = userRepository.getByIdWithRoles(userId);

        if (user == null) {
            throw new EntityNotFoundException("User", "user.id", userId);
        }

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("admin"));

        if (isAdmin) {
            throw new AdminRoleManagementException(ALREADY_ADMIN_ERROR_MESSAGE);
        }

        user.addRole(roleRepository.findByName("ADMIN"));

        userRepository.update(user);

        if (phoneNumber != null) {
            AdminDetails adminDetails = new AdminDetails(user, phoneNumber);

            adminDetailsRepository.create(adminDetails);
        }
    }

    @Override
    public void update(AdminDetails admin, User userRequest) {
        authorization(userRequest);

        AdminDetails databaseAdminDetails = adminDetailsRepository.getByUserId(admin.getUser().getId());

        if (databaseAdminDetails == null) {
            throw new EntityNotFoundException("Admin", "id", admin.getUser().getId());
        }

        if (admin.getPhoneNumber() == null && databaseAdminDetails.getPhoneNumber() != null) {
            adminDetailsRepository.delete(admin.getUser().getId());
        } else if (databaseAdminDetails.getPhoneNumber() != null) {
            adminDetailsRepository.update(admin);
        } else {
            adminDetailsRepository.create(admin);
        }
    }

    private void authorization(User userRequest) {

        //|| userRequest.getRoles().stream()
        //                .noneMatch(role -> role.getName()
        //                        .equalsIgnoreCase("admin"))
        if (userRequest.isBlocked()) {
            throw new UnauthorizedOperationException(PERMISSION_ERROR_MESSAGE);

        }
    }
}
