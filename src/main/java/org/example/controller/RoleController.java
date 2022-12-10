package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.mapper.RoleMapper;
import org.example.model.CreateRoleInput;
import org.example.model.entity.Role;
import org.example.model.entity.User;
import org.example.service.RoleService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @MutationMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Role createRole(@Argument CreateRoleInput createRoleInput){
        return roleService.saveRole(RoleMapper.INSTANCE.createRoleInputToRole(createRoleInput));
    }

    @MutationMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public User addRoleToUser(@Argument String username, @Argument String roleName){
        return roleService.addRoleToUser(username, roleName);
    }

}