package com.telerik.forum.services;


import com.telerik.forum.models.User;
import java.util.List;

public interface UserService {

    List<User> getAll();

    User getById(int id);

    User getByEmail(String email);

    User getByUsername(String username);

    User getByFirstName(String firstName);

    void create(User userInput, User requestUser);

    void update(User userInput, User requestUser);

    void delete(int id, User requestUser);

}
