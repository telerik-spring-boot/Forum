package com.telerik.forum.repositories;

import com.telerik.forum.models.AdminDetails;

import java.util.List;

public interface AdminDetailsRepository {

    List<AdminDetails> getAll();

    AdminDetails getByUserId(int id);

    void create(AdminDetails admin);

    void update(AdminDetails admin);

    void delete(int userId);
}
