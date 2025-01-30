package com.telerik.forum.services.user;


import com.telerik.forum.models.user.User;
import com.telerik.forum.models.filters.FilterCommentOptions;
import com.telerik.forum.models.filters.FilterPostOptions;

public interface UserService {


    User getById(int id);

    User getByIdWithPosts(int id, FilterPostOptions options);

    User getByIdWithComments(int id, FilterCommentOptions options);

    User getByEmail(String email);

    User getByUsername(String username);

    User getByUsernameWithRoles(String username);

    User getByFirstName(String firstName);

    void create(User userInput);

    void update(User userInput, int requestUserId);

    void delete(int id, int requestUserId);

}
