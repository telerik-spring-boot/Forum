package com.telerik.forum.controllers.mvc;


import com.telerik.forum.helpers.AuthenticationHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AnonymousMvcController {

    private final AuthenticationHelper authenticationHelper;

    public AnonymousMvcController(AuthenticationHelper authenticationHelper) {
        this.authenticationHelper = authenticationHelper;
    }


}
