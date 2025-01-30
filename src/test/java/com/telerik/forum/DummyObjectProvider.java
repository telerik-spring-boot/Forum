package com.telerik.forum;

import com.telerik.forum.models.dtos.postDTOs.PostCreateDTO;
import com.telerik.forum.models.dtos.tagDTOs.TagCreateAndDeleteDTO;
import com.telerik.forum.models.post.Comment;
import com.telerik.forum.models.post.Like;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.post.Tag;
import com.telerik.forum.models.user.AdminDetails;
import com.telerik.forum.models.user.Role;
import com.telerik.forum.models.user.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DummyObjectProvider {

    public static User createMockUser(){
        User user = new User();

        user.setId(1);
        user.setFirstName("George");
        user.setLastName("Bush");
        user.setEmailAddress("george.bush@gmail.com");
        user.setUsername("george_bush");
        user.setPassword("Password123!");
        user.setBlocked(false);
        Set<Role> roles = new HashSet<>();
        roles.add(new Role("USER"));
        user.setRoles(roles);

        return user;
    }

    public static AdminDetails createMockAdminDetails(){
        AdminDetails adminDetails = new AdminDetails();

        adminDetails.setId(1);
        User user = createMockUser();
        Role role = new Role("ADMIN");
        role.setId(1);
        user.addRole(role);
        adminDetails.setUser(user);
        adminDetails.setPhoneNumber("123456789");

        return adminDetails;
    }

    public static Post createMockPost(){
        Post post = new Post();

        post.setId(1);
        post.setTitle("This is the title of a mock post.");
        post.setContent("This is the content of a mock post.");
        post.setCreatedAt(LocalDateTime.now());

        return post;
    }

    public static Comment createMockComment(){
        Comment comment = new Comment();

        comment.setId(1);
        comment.setContent("This is the content of a mock comment.");
        comment.setCreatedAt(LocalDateTime.now());

        return comment;

    }

    public static Tag createMockTag(){
        Tag tag = new Tag();

        tag.setId(1);
        tag.setName("mocktag");

        return tag;
    }

    public static Like createMockLike(){
        Like like = new Like();

        like.setId(1);
        like.setReaction(1);

        return like;
    }

    public static PostCreateDTO createMockPostCreateDTO(){
        PostCreateDTO postCreateDTO = new PostCreateDTO();

        postCreateDTO.setTitle("This is the title of a mock post.");
        postCreateDTO.setContent("This is the content of a mock post that is passing the validation requirements.");

        return postCreateDTO;
    }

}
