package com.telerik.forum.repositories;

import com.telerik.forum.models.Role;

public interface RoleRepository {
    Role findByName(String name);
}
