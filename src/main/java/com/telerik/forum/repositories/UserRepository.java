package com.telerik.forum.repositories;

import com.telerik.forum.models.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository {

    List<User> getAll();

    User getById(int id);

    User getByEmail(String email);

    User getByUsername(String username);

    User getByFirstName(String firstName);

    void create(User user);

    void update(User user);

    void delete(int id);
}
