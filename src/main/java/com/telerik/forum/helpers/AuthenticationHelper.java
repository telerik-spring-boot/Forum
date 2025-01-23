package com.telerik.forum.helpers;


import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.User;
import com.telerik.forum.services.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Base64;


//TODO to be updated -> inserted for basic logic handling
@Component
public class AuthenticationHelper {
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private final UserService userService;

    public AuthenticationHelper(UserService userService) {
        this.userService = userService;
    }

    public User tryGetUser(HttpHeaders headers) {
        if (!headers.containsKey(AUTHORIZATION_HEADER_NAME)) {
            throw new UnauthorizedOperationException("The requested resource requires authentication.");
        }

        try {
            String authorization = headers.getFirst(AUTHORIZATION_HEADER_NAME);

            if (authorization == null) {
                throw new UnauthorizedOperationException("The requested resource requires authentication.");
            }

            String[] authentication = authorization.split(" ");

            String[] authenticationCredentials = new String(Base64.getDecoder().decode(authentication[1])).split(":");

            User user = userService.getByEmail(authenticationCredentials[0]);

            if (!user.getPassword().equals(authenticationCredentials[1])) {
                throw new UnauthorizedOperationException("Invalid password.");
            }

            return user;

        } catch (EntityNotFoundException e) {
            throw new UnauthorizedOperationException("Invalid username");
        }

    }
}