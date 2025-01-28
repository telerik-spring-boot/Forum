package com.telerik.forum.services.admin;

import com.telerik.forum.models.user.AdminDetails;
import com.telerik.forum.models.user.User;
import com.telerik.forum.models.filters.FilterUserOptions;

import java.util.List;

public interface AdminService {

    List<AdminDetails> getAll();

    List<User> getAllUsers(FilterUserOptions options, int requestUserId);

    AdminDetails getByUserId(int id);

    void blockUser(User userToBeBlocked, int requestUserId);

    void unblockUser(User userToBeUnblocked, int requestUserId);

    void revokeAdminRights(int userId, int requestUserId);

    void giveAdminRights(int userId, String phoneNumber, int requestUserId);

    void update(AdminDetails admin, int requestUserId);

}
