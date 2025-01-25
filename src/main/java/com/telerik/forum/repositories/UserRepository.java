package com.telerik.forum.repositories;

import com.telerik.forum.models.User;
import com.telerik.forum.models.filters.FilterUserOptions;

import java.util.List;

public interface UserRepository {

    List<User> getAll(FilterUserOptions options);

    User getById(int id);

    User getByIdWithPosts(int id);

    User getByIdWithComments(int id);

    User getByIdWithRoles(int id);

    User getByEmail(String email);

    User getByUsername(String username);

    User getByFirstName(String firstName);

    void create(User user);

    void update(User user);

    void delete(int id);
}
