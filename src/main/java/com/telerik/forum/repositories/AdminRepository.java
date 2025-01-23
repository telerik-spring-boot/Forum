package com.telerik.forum.repositories;

import com.telerik.forum.models.Admin;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRepository {

    List<Admin> getAll();

    Admin getByUserId(int id);

    void create(Admin admin);

    void update(Admin admin);

    void delete(int userId);
}
