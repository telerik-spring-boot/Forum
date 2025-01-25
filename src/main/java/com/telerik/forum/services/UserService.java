package com.telerik.forum.services;


import com.telerik.forum.models.User;
import java.util.List;

public interface UserService {


    User getById(int id);

    User getByIdWithPosts(int id);

    User getByIdWithComments(int id);

    User getByEmail(String email);

    User getByUsername(String username);

    User getByFirstName(String firstName);

    void create(User userInput, int requestUserId);

    void update(User userInput, int requestUserId);

    void delete(int id, int requestUserId);

}
