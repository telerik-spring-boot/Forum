package com.telerik.forum.configurations;

import com.telerik.forum.models.filters.FilterUserOptions;
import com.telerik.forum.models.user.User;
import com.telerik.forum.repositories.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PasswordMigrationRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordMigrationRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        List<User> users = userRepository.getAll(new FilterUserOptions(null, null, null, null,null));
        for (User user : users) {
            if (!user.getPassword().startsWith("$2a$")) { // Check if already encoded (BCrypt hashes start with "$2a$")
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepository.update(user);
            }
        }
    }
}
