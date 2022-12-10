package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.entity.Role;
import org.example.model.entity.User;
import org.example.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService{

    private final RoleRepository roleRepository;

    private final UserService userService;

    @Override
    public Role saveRole(Role role) {
        log.info("Saving new Role {} to the database", role.getName());
        return roleRepository.save(role);
    }

    @Override
    public User addRoleToUser(String username, String roleName) {
        User user = userService.getUser(username);
        Role role = roleRepository.findByName(roleName);
        log.info("Adding new Role {} to the user {}", role.getName(), user.getUsername());
        user.getRoleList().add(role);
        return userService.saveOrUpdate(user);
    }

    @Override
    public Role findRoleByName(String name) {
        return roleRepository.findByName(name);
    }
}