package com.telerik.forum.services;

import com.telerik.forum.models.Admin;
import com.telerik.forum.models.User;

import java.util.List;

public interface AdminServiceOld {


    List<Admin> getAll();

    Admin getByUserId(int id);

    void blockUser(User userToBeBlocked, int requestUserId);

    void unblockUser(User userToBeUnblocked, int requestUserId);

    void create(User user, String phoneNumber, int requestUserId);

    void update(Admin admin, int requestUserId);

    void delete(int id, int requestUserId);
}
