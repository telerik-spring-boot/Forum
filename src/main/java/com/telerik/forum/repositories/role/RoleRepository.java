package com.telerik.forum.repositories.role;

import com.telerik.forum.models.user.Role;

public interface RoleRepository {
    Role findByName(String name);
}
