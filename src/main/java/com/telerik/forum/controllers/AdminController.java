package com.telerik.forum.controllers;


import com.telerik.forum.exceptions.AdminRoleManagementException;
import com.telerik.forum.exceptions.DuplicateEntityException;
import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.helpers.UserMapper;
import com.telerik.forum.models.Admin;
import com.telerik.forum.models.AdminDetails;
import com.telerik.forum.models.User;
import com.telerik.forum.models.dtos.adminDTOs.AdminCreateDTO;
import com.telerik.forum.models.dtos.adminDTOs.AdminDisplayDTO;
import com.telerik.forum.models.dtos.adminDTOs.AdminUpdateDTO;
import com.telerik.forum.models.dtos.userDTOs.UserDisplayDTO;
import com.telerik.forum.models.filters.FilterUserOptions;
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

    @GetMapping("/users")
    public List<UserDisplayDTO> getAllUsers(@RequestHeader HttpHeaders headers,
                                            @RequestParam(required = false) String username,
                                            @RequestParam(required = false) String email,
                                            @RequestParam(required = false) String firstName,
                                            @RequestParam(required = false) String sortBy,
                                            @RequestParam(required = false) String sortOrder) {
        try{
            User userRequest = authenticationHelper.tryGetUser(headers);

            return adminService.getAllUsers(new FilterUserOptions(username, email, firstName, sortBy, sortOrder), userRequest.getId())
                    .stream()
                    .map(userMapper::userToUserDisplayDTO)
                    .toList();
        }catch (UnauthorizedOperationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    @GetMapping("/{userId}")
    public AdminDisplayDTO getAdminByUserId(@PathVariable int userId) {
        try {
            return userMapper.AdminToAdminDisplayDTO(adminService.getByUserId(userId));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/users/rights")
    public AdminDisplayDTO createAdmin(@RequestHeader HttpHeaders headers,@Valid @RequestBody AdminCreateDTO adminDTO) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);

            adminService.giveAdminRights(adminDTO.getUser_id(), adminDTO.getPhoneNumber(), userRequest.getId());

            return userMapper.AdminToAdminDisplayDTO(adminService.getByUserId(adminDTO.getUser_id()));

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/users/rights/{userId}")
    public void revokeAdminRights(@RequestHeader HttpHeaders headers, @PathVariable int userId) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);

            adminService.revokeAdminRights(userId, userRequest.getId());

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }catch(AdminRoleManagementException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PutMapping("/{userId}")
    public AdminDisplayDTO updateAdmin(@RequestHeader HttpHeaders headers, @PathVariable int userId, @Valid @RequestBody AdminUpdateDTO adminDTO) {
        try {
            User userRequest = authenticationHelper.tryGetUser(headers);

            AdminDetails admin = userMapper.dtoToAdmin(userId, adminDTO);

            adminService.update(admin, userRequest.getId());

            return userMapper.AdminToAdminDisplayDTO(admin);

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/users/{userId}/block")
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

    @PutMapping("/users/{userId}/unblock")
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
}
