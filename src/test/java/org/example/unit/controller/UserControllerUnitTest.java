package org.example.unit.controller;

import org.example.config.security.RsaKeyProperties;
import org.example.controller.UserController;
import org.example.model.entity.User;
import org.example.service.RoleService;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@GraphQlTest(controllers = UserController.class)
public class UserControllerUnitTest {

    @Autowired
    GraphQlTester graphQlTester;

    @MockBean
    RsaKeyProperties rsaKeyProperties;

    @MockBean
    RoleService roleService;

    @MockBean
    UserService userService;

    @Test
    void testGetUsers() {
        User user1 = new User();
        user1.setId("1");
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId("2");
        user2.setUsername("user2");
        doReturn(List.of(user1, user2)).when(userService).getUsers();

        //language=GraphQL
        String document = """
                query{
                   getUsers{
                     id
                     name
                     surname
                     password
                     username
                     tournamentId
                     roleList{
                       id
                       name
                     }
                   }
                 }
                """;

        graphQlTester.document(document)
                .execute()
                .path("getUsers")
                .entityList(User.class)
                .satisfies(users -> {
                    assertThat(users.size()).isEqualTo(2);
                    users.forEach(user -> assertThat(user.getId()).isNotNull());
                });
    }

    @Test
    @WithMockUser(username = "user1")
    void testEditUser() {
        User user1 = new User();
        user1.setId("1");
        user1.setUsername("user1");
        user1.setName("Edited Name");

        doReturn(user1).when(userService).editUser(any(), any());

        //language=GraphQL
        String document = """
                mutation($userId : String!){
                        editUser(editUserInput: {
                          id : $userId
                          name: "Edited Name"
                        })
                        {
                          id
                          name
                          password
                        }
                      }
                """;

        graphQlTester.document(document)
                .variable("userId", user1.getId())
                .execute()
                .path("editUser")
                .entity(User.class)
                .satisfies(user -> {
                    assertThat(user.getName()).isEqualTo("Edited Name");
                });
    }

    @Test
    void testRegisterUser() {
        User registeredUser = new User();
        registeredUser.setId("1");
        registeredUser.setUsername("user1");
        registeredUser.setName("name");
        registeredUser.setSurname("surname");
        registeredUser.setPassword("Encrypted Password");

        doReturn(registeredUser).when(userService).createUser(any());

        //language=GraphQL
        String document = """
                mutation{
                        registerUser(
                          registerUserInput : {
                            name: "name"
                            surname: "surname"
                            password: "Encrypted Password"
                            username: "user1"
                          })
                        {
                          id
                          name
                          surname
                          password
                          username
                        }
                      }
                """;

        graphQlTester.document(document)
                .execute()
                .path("registerUser")
                .entity(User.class)
                .satisfies(user -> {
                    assertThat(user.getName()).isEqualTo("name");
                    assertThat(user.getSurname()).isEqualTo("surname");
                    assertThat(user.getPassword()).isEqualTo("Encrypted Password");
                    assertThat(user.getUsername()).isEqualTo("user1");
                });
    }

}
