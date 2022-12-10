package org.example.unit.service;

import org.example.model.entity.Role;
import org.example.model.entity.User;
import org.example.repository.RoleRepository;
import org.example.service.RoleService;
import org.example.service.RoleServiceImpl;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class RoleServiceUnitTest {

    @Mock
    RoleRepository roleRepository;

    @Mock
    UserService userService;

    @InjectMocks
    RoleServiceImpl roleService;

    @Test
    void givenRole_whenSaveRole_thenReturnRole(){
        Role returnRole = new Role("1", "ROLE_TEST");
        doReturn(returnRole).when(roleRepository).save(returnRole);

        Role savedRole = roleService.saveRole(returnRole);

        assertThat(savedRole.getName()).isEqualTo("ROLE_TEST");
    }

    @Test
    void givenUserName_andGivenRoleName_whenAddRoleToUser_thenReturnUser(){
        Role role1 = new Role("1", "ROLE_USER");
        Role role2 = new Role("2", "ROLE_ADMIN");

        User beforeSaveUser = new User();
        beforeSaveUser.setId("1");
        beforeSaveUser.setUsername("user1");
        beforeSaveUser.getRoleList().add(role1);

        User returnSaveUser = new User();
        returnSaveUser.setId(beforeSaveUser.getId());
        returnSaveUser.setUsername(beforeSaveUser.getUsername());
        returnSaveUser.getRoleList().addAll(beforeSaveUser.getRoleList());
        returnSaveUser.getRoleList().add(role2);


        doReturn(role2).when(roleRepository).findByName("ROLE_ADMIN");
        doReturn(beforeSaveUser).when(userService).getUser("user1");
        doReturn(returnSaveUser).when(userService).saveOrUpdate(returnSaveUser);

        User savedUser = roleService.addRoleToUser("user1", "ROLE_ADMIN");

        assertThat(savedUser.getRoleList().size()).isEqualTo(2);
        assertThat(savedUser.getRoleList()).containsAll(List.of(role1, role2));
    }

    @Test
    void givenRoleName_whenFindRoleByName_thenReturnRole(){
        Role returnRole = new Role("1", "ROLE_USER");

        doReturn(returnRole).when(roleRepository).findByName("ROLE_USER");

        Role returnedRole = roleService.findRoleByName("ROLE_USER");

        assertThat(returnedRole.getId()).isEqualTo("1");
        assertThat(returnedRole.getName()).isEqualTo("ROLE_USER");
    }

}
