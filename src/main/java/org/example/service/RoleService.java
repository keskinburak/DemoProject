package org.example.service;

import org.example.model.entity.Role;
import org.example.model.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface RoleService {

    Role saveRole(Role role);

    User addRoleToUser(String userName, String roleName);

    Role findRoleByName(String name);

}
