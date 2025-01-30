package com.telerik.forum.helpers;


import com.telerik.forum.configurations.jwt.JwtUtil;
import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.user.User;
import com.telerik.forum.services.user.UserService;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationHelper {

    private static final String AUTHENTICATION_ERROR_MESSAGE = "The requested resource requires authentication.";
    private static final String WRONG_CREDENTIALS_ERROR_MESSAGE = "Invalid credentials.";

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthenticationHelper(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    public User tryGetUser(HttpHeaders headers) {
        return userService.getByUsername(validateTokenAndReturnUsername(headers));
    }

    public User tryGetUserWithRoles(HttpHeaders headers) {
        return userService.getByUsernameWithRoles(validateTokenAndReturnUsername(headers));
    }

    private String validateTokenAndReturnUsername(HttpHeaders headers){
        String authorizationHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedOperationException(AUTHENTICATION_ERROR_MESSAGE);
        }

        try {
            String token = authorizationHeader.substring(7);

            return jwtUtil.extractUsername(token);
        } catch (EntityNotFoundException | JwtException e) {
            throw new UnauthorizedOperationException(WRONG_CREDENTIALS_ERROR_MESSAGE);
        }

    }
}