package com.telerik.forum;

import com.telerik.forum.models.post.Comment;
import com.telerik.forum.models.post.Like;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.post.Tag;
import com.telerik.forum.models.user.AdminDetails;
import com.telerik.forum.models.user.Role;
import com.telerik.forum.models.user.User;

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
        user.setRoles(Set.of(new Role("USER")));
        user.setComments(List.of(createMockComment()));
        user.setPosts(List.of(createMockPost()));

        return user;
    }

    public static AdminDetails createMockAdminDetails(){
        AdminDetails adminDetails = new AdminDetails();

        adminDetails.setId(1);
        User user = createMockUser();
        user.addRole(new Role("ADMIN"));
        adminDetails.setUser(user);
        adminDetails.setPhoneNumber("123456789");

        return adminDetails;
    }

    public static Post createMockPost(){
        Post post = new Post();

        post.setId(1);
        post.setUser(createMockUser());
        post.setTitle("This is the title of a mock post.");
        post.setContent("This is the content of a mock post.");
        post.setTags(Set.of(createMockTag()));
        post.setComments(Set.of(createMockComment()));
        post.setLikes(Set.of(createMockLike()));

        return post;
    }

    public static Comment createMockComment(){
        Comment comment = new Comment();

        comment.setId(1);
        comment.setUser(createMockUser());
        comment.setContent("This is the content of a mock comment.");
        comment.setPost(createMockPost());

        return comment;

    }

    public static Tag createMockTag(){
        Tag tag = new Tag();

        tag.setId(1);
        tag.setName("Mock Tag");

        return tag;
    }

    public static Like createMockLike(){
        Like like = new Like();

        like.setId(1);
        like.setUser(createMockUser());
        like.setPost(createMockPost());
        like.setReaction(1);

        return like;
    }
}
