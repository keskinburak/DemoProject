package org.example.unit.controller;

import org.example.config.security.RsaKeyProperties;
import org.example.controller.RoleController;
import org.example.model.entity.Role;
import org.example.model.entity.User;
import org.example.service.RoleService;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@GraphQlTest(controllers = RoleController.class)
public class RoleControllerUnitTest {

    @Autowired
    GraphQlTester graphQlTester;

    @MockBean
    RoleService roleService;

    @MockBean
    UserService userService;

    @MockBean
    RsaKeyProperties rsaKeyProperties;

    @Test
//    @WithMockUser(username = "adminUser", roles = {"ADMIN"})
    void testCreateRole() {
        Role returnRole = new Role("1", "ROLE_TEST");
        doReturn(returnRole).when(roleService).saveRole(any());
        //language=GraphQL
        String document = """
                mutation {
                    createRole
                    (createRoleInput:
                      {
                        name: "ROLE_TEST"
                      }
                    ) {
                      id
                      name
                    }
                  }
                """;

        graphQlTester.document(document)
                .execute()
                .path("createRole")
                .entity(Role.class)
                .satisfies(role -> {
                    assertThat(role.getId()).isNotNull();
                    assertThat(role.getName()).isEqualTo("ROLE_TEST");
                });
    }

    @Test
    void testAddRoleToUser() {
        Role role1 = new Role("1", "ROLE_USER");
        Role role2 = new Role("2", "ROLE_ADMIN");

        User returnUser = new User();
        returnUser.setId("1");
        returnUser.setUsername("user1");
        returnUser.getRoleList().addAll(List.of(role1, role2));

        doReturn(returnUser).when(roleService).addRoleToUser("user1", "ROLE_ADMIN");
        //language=GraphQL
        String document = """
                mutation {
                          addRoleToUser
                          (username : "user1", roleName: "ROLE_ADMIN") {
                            id
                            name
                            roleList{
                              id
                              name
                            }
                          }
                        }
                """;

        graphQlTester.document(document)
                .execute()
                .path("addRoleToUser")
                .entity(User.class)
                .satisfies(user -> assertThat(user.getRoleList()).anyMatch(role -> role.getName().equals("ROLE_ADMIN")));
    }
}
