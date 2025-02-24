package com.telerik.forum.services.user;


import com.telerik.forum.models.dtos.userDTOs.UserOverviewPageDisplayDTO;
import com.telerik.forum.models.dtos.userDTOs.UserPostsPageDisplayDTO;
import com.telerik.forum.models.filters.FilterCommentOptions;
import com.telerik.forum.models.filters.FilterPostOptions;
import com.telerik.forum.models.user.User;
import org.springframework.data.domain.Pageable;

public interface UserService {


    User getById(int id, User userRequest);

    User getByIdWithRoles(int id, User userRequest);

    UserPostsPageDisplayDTO getByIdWithPosts(int id, FilterPostOptions options, User userRequest, Pageable pageable);

    User getByIdWithComments(int id, FilterCommentOptions options, User userRequest);

    User getByUsername(String username);

    User getByUsernameWithRoles(String username);

    UserOverviewPageDisplayDTO getByIdWithCommentsAndPosts(int id, FilterPostOptions postOptions, FilterCommentOptions commentOptions, User userRequest, Pageable pageable);

    void create(User userInput);

    void update(User userInput, User userRequest);

    void delete(int id, User userRequest);

}
