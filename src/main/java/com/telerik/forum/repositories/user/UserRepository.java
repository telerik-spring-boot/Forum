package com.telerik.forum.repositories.user;

import com.telerik.forum.models.dtos.userDTOs.UserDisplayMvcDTO;
import com.telerik.forum.models.filters.FilterUserOptions;
import com.telerik.forum.models.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserRepository {

    Page<User> getAll(FilterUserOptions options, Pageable pageable);

    List<User> getAll();

    List<UserDisplayMvcDTO> getAllMvc();

    User getById(int id);

    User getByIdWithPosts(int id);

    User getByIdWithComments(int id);

    User getByIdWithRoles(int id);

    User getByEmail(String email);

    User getByUsername(String username);

    User getByUsernameWithRoles(String username);

    User getByFirstName(String firstName);

    void create(User user);

    void update(User user);

    void delete(int id);
}
