package com.telerik.forum;

import com.telerik.forum.models.user.Role;
import com.telerik.forum.models.user.User;

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
        user.addRole(new Role("USER"));

        return user;
    }
}
