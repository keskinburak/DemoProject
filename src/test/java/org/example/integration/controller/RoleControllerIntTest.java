package org.example.integration.controller;

import org.example.DBTestConfig;
import org.example.model.entity.Role;
import org.example.model.entity.User;
import org.example.repository.RoleRepository;
import org.example.repository.TournamentRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureGraphQlTester
public class RoleControllerIntTest extends DBTestConfig {

    @Autowired
    TournamentRepository tournamentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    GraphQlTester graphQlTester;

    @Test
    @WithMockUser(username = "adminUser", roles = {"ADMIN"})
    void testCreateRole() {
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
    @WithMockUser(username = "adminUser", roles = {"ADMIN"})
    void testAddRoleToUser() {
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