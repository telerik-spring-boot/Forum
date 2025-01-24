package com.telerik.forum.controllers;


import com.telerik.forum.exceptions.DuplicateEntityException;
import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.helpers.UserMapper;
import com.telerik.forum.models.Admin;
import com.telerik.forum.models.User;
import com.telerik.forum.models.dtos.adminDTOs.AdminCreateDTO;
import com.telerik.forum.models.dtos.adminDTOs.AdminDisplayDTO;
import com.telerik.forum.models.dtos.userDTOs.UserDisplayDTO;
import com.telerik.forum.services.AdminService;
import com.telerik.forum.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final UserMapper userMapper;

    @Autowired
    public AdminController(AdminService adminService, UserService userService, AuthenticationHelper authenticationHelper, UserMapper userMapper) {
        this.adminService = adminService;
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.userMapper = userMapper;
    }

    @GetMapping
    public List<AdminDisplayDTO> getAllAdmins() {
        return adminService.getAll().stream()
                .map(userMapper::AdminToAdminDisplayDTO)
                .toList();
    }

    @GetMapping("/{userId}")
    public AdminDisplayDTO getAdminByUserId(@PathVariable int userId) {
        try {
            return userMapper.AdminToAdminDisplayDTO(adminService.getByUserId(userId));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    public AdminDisplayDTO createAdmin(@RequestHeader HttpHeaders headers,@Valid @RequestBody AdminCreateDTO adminDTO) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);

            adminService.create(userService.getById(adminDTO.getUser_id()), adminDTO.getPhone_number(), userRequest.getId());

            return userMapper.AdminToAdminDisplayDTO(adminService.getByUserId(adminDTO.getUser_id()));

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
    public UserDisplayDTO blockUser(@RequestHeader HttpHeaders headers, @PathVariable int userId) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);

            User userToBlock = userService.getById(userId);
            adminService.blockUser(userToBlock, userRequest.getId());

            return userMapper.userToUserDisplayDTO(userToBlock);

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/unblock/{userId}")
    public UserDisplayDTO unblockUser(@RequestHeader HttpHeaders headers, @PathVariable int userId) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);

            User userToUnblock = userService.getById(userId);
            adminService.unblockUser(userToUnblock, userRequest.getId());

            return userMapper.userToUserDisplayDTO(userToUnblock);
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
