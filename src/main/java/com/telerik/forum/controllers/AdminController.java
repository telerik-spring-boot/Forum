package com.telerik.forum.controllers;


import com.telerik.forum.exceptions.DuplicateEntityException;
import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.models.Admin;
import com.telerik.forum.models.User;
import com.telerik.forum.models.dtos.AdminCreateDTO;
import com.telerik.forum.services.AdminService;
import com.telerik.forum.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public AdminController(AdminService adminService, UserService userService, AuthenticationHelper authenticationHelper) {
        this.adminService = adminService;
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public List<Admin> getAllAdmins() {
        return adminService.getAll();
    }

    @GetMapping("/{userId}")
    public Admin getAdminByUserId(@PathVariable int userId) {
        try {
            return adminService.getByUserId(userId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    public Admin createAdmin(@RequestHeader HttpHeaders headers, @RequestBody AdminCreateDTO adminDTO) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);

            adminService.create(userService.getById(adminDTO.getUser_id()), adminDTO.getPhone_number(), userRequest.getId());

            return adminService.getByUserId(adminDTO.getUser_id());

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{userId}")
    public Admin updateAdmin(@RequestHeader HttpHeaders headers, @PathVariable int userId, @RequestBody AdminCreateDTO adminDTO) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);

            Admin admin = adminService.getByUserId(userId);
            admin.setPhoneNumber(adminDTO.getPhone_number());
            adminService.update(admin, userRequest.getId());

            //TODO adjust with DTO later on

            return admin;

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/block/{userId}")
    public void blockUser(@RequestHeader HttpHeaders headers, @PathVariable int userId) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);

            adminService.blockUser(userService.getById(userId), userRequest.getId());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/unlock/{userId}")
    public void unlockUser(@RequestHeader HttpHeaders headers, @PathVariable int userId) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);

            adminService.unblockUser(userService.getById(userId), userRequest.getId());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    public void deleteAdmin(@RequestHeader HttpHeaders headers, @PathVariable int userId) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);

            adminService.delete(userId, userRequest.getId());

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
