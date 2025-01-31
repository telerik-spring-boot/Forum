package com.telerik.forum.controllers;


import com.telerik.forum.helpers.AuthenticationHelper;
import com.telerik.forum.helpers.UserMapper;
import com.telerik.forum.models.dtos.adminDTOs.AdminCreateDTO;
import com.telerik.forum.models.dtos.adminDTOs.AdminDisplayDTO;
import com.telerik.forum.models.dtos.adminDTOs.AdminUpdateDTO;
import com.telerik.forum.models.dtos.userDTOs.UserDisplayDTO;
import com.telerik.forum.models.filters.FilterUserOptions;
import com.telerik.forum.models.user.AdminDetails;
import com.telerik.forum.models.user.User;
import com.telerik.forum.services.admin.AdminService;
import com.telerik.forum.services.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

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
    public List<AdminDisplayDTO> getAllAdmins(@RequestHeader HttpHeaders headers) {

        User userRequest = authenticationHelper.tryGetUserWithRoles(headers);

        return adminService.getAll(userRequest).stream().map(userMapper::adminToAdminDisplayDTO).toList();

    }

    @GetMapping("/users")
    public List<UserDisplayDTO> getAllUsers(@RequestHeader HttpHeaders headers, @RequestParam(required = false) String username, @RequestParam(required = false) String emailAddress, @RequestParam(required = false) String firstName, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortOrder) {

        User userRequest = authenticationHelper.tryGetUserWithRoles(headers);

        return adminService.getAllUsers(new FilterUserOptions(username, emailAddress, firstName, sortBy, sortOrder), userRequest).stream().map(userMapper::userToUserDisplayDTO).toList();


    }

    @GetMapping("/{userId}")
    public AdminDisplayDTO getAdminByUserId(@RequestHeader HttpHeaders header, @PathVariable int userId) {

        User userRequest = authenticationHelper.tryGetUserWithRoles(header);

        return userMapper.adminToAdminDisplayDTO(adminService.getByUserId(userId, userRequest));

    }

    @PostMapping("/users/rights")
    public AdminDisplayDTO createAdmin(@RequestHeader HttpHeaders headers, @Valid @RequestBody AdminCreateDTO adminDTO) {

        User userRequest = authenticationHelper.tryGetUserWithRoles(headers);

        adminService.giveAdminRights(adminDTO.getUser_id(), adminDTO.getPhoneNumber(), userRequest);

        return userMapper.adminToAdminDisplayDTO(adminService.getByUserId(adminDTO.getUser_id(), userRequest));


    }

    @DeleteMapping("/users/rights/{userId}")
    public void revokeAdminRights(@RequestHeader HttpHeaders headers, @PathVariable int userId) {

        User userRequest = authenticationHelper.tryGetUserWithRoles(headers);

        adminService.revokeAdminRights(userId, userRequest);


    }

    @PutMapping("/{userId}")
    public AdminDisplayDTO updateAdmin(@RequestHeader HttpHeaders headers, @PathVariable int userId, @Valid @RequestBody AdminUpdateDTO adminDTO) {

        User userRequest = authenticationHelper.tryGetUserWithRoles(headers);

        AdminDetails admin = userMapper.dtoToAdmin(userId, adminDTO, userRequest);

        adminService.update(admin, userRequest);

        return userMapper.adminToAdminDisplayDTO(admin);


    }

    @PutMapping("/users/{userId}/block")
    public UserDisplayDTO blockUser(@RequestHeader HttpHeaders headers, @PathVariable int userId) {

        User userRequest = authenticationHelper.tryGetUserWithRoles(headers);

        User userToBlock = userService.getById(userId, userRequest);
        adminService.blockUser(userToBlock, userRequest);

        return userMapper.userToUserDisplayDTO(userToBlock);


    }

    @PutMapping("/users/{userId}/unblock")
    public UserDisplayDTO unblockUser(@RequestHeader HttpHeaders headers, @PathVariable int userId) {

        User userRequest = authenticationHelper.tryGetUserWithRoles(headers);

        User userToUnblock = userService.getById(userId, userRequest);
        adminService.unblockUser(userToUnblock, userRequest);

        return userMapper.userToUserDisplayDTO(userToUnblock);

    }
}
