package com.telerik.forum.repositories;

import com.telerik.forum.models.Admin;

import java.util.List;

public interface AdminRepository {

    List<Admin> getAll();

    Admin getByUserId(int id);

    boolean existsById(int id);

    void create(Admin admin);

    void update(Admin admin);

    void delete(int userId);
}
