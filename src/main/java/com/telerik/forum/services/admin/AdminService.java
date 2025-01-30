package com.telerik.forum.services.admin;

import com.telerik.forum.models.user.AdminDetails;
import com.telerik.forum.models.user.User;
import com.telerik.forum.models.filters.FilterUserOptions;

import java.util.List;

public interface AdminService {

    List<AdminDetails> getAll(User userRequest);

    List<User> getAllUsers(FilterUserOptions options, User userRequest);

    AdminDetails getByUserId(int id, User userRequest);

    void blockUser(User userToBeBlocked, User userRequest);

    void unblockUser(User userToBeUnblocked, User userRequest);

    void revokeAdminRights(int userId, User userRequest);

    void giveAdminRights(int userId, String phoneNumber, User userRequest);

    void update(AdminDetails admin, User userRequest);

}
