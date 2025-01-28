package com.telerik.forum.helpers;


import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.user.User;
import com.telerik.forum.services.user.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Base64;


//TODO to be updated -> inserted for basic logic handling
@Component
public class AuthenticationHelper {
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String AUTHENTICATION_ERROR_MESSAGE = "The requested resource requires authentication.";
    private static final String WRONG_CREDENTIALS_ERROR_MESSAGE = "Invalid credentials.";
    private final UserService userService;

    public AuthenticationHelper(UserService userService) {
        this.userService = userService;
    }

    public User tryGetUser(HttpHeaders headers) {
        if (!headers.containsKey(AUTHORIZATION_HEADER_NAME)) {
            throw new UnauthorizedOperationException(AUTHENTICATION_ERROR_MESSAGE);
        }

        try {
            String authorization = headers.getFirst(AUTHORIZATION_HEADER_NAME);

            if (authorization == null) {
                throw new UnauthorizedOperationException(AUTHENTICATION_ERROR_MESSAGE);
            }

            String[] authentication = authorization.split(" ");

            String[] authenticationCredentials = new String(Base64.getDecoder().decode(authentication[1])).split(":");

            User user = userService.getByUsername(authenticationCredentials[0]);

            if (!user.getPassword().equals(authenticationCredentials[1])) {
                throw new UnauthorizedOperationException(WRONG_CREDENTIALS_ERROR_MESSAGE);
            }

            return user;

        } catch (EntityNotFoundException e) {
            throw new UnauthorizedOperationException(WRONG_CREDENTIALS_ERROR_MESSAGE);
        }

    }
}