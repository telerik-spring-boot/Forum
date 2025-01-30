package com.telerik.forum.helpers;

import com.telerik.forum.configurations.jwt.JwtUtil;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.dtos.userDTOs.UserLoginDTO;
import com.telerik.forum.models.user.User;
import com.telerik.forum.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class LoginHelper {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LoginHelper(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        User user = userService.getByUsername(username);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedOperationException("Invalid username or password");
        }

        return jwtUtil.generateToken(username);
    }
}
