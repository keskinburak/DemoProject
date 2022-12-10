package org.example.integration.controller;

import org.example.DBTestConfig;
import org.example.model.entity.Tournament;
import org.example.model.entity.User;
import org.example.repository.TournamentRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureGraphQlTester
public class UserControllerIntTest extends DBTestConfig {

    @Autowired
    TournamentRepository tournamentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GraphQlTester graphQlTester;

    @Test
    void testGetUsers() {

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
                .satisfies(user -> assertThat(user.size()).isGreaterThanOrEqualTo(3));
    }

    @Test
    @WithMockUser(username = "user3")
    void testEditUser_withRightUser() {
        User userEntity = userRepository.findUserByUsername("user3");
        //language=GraphQL
        String document = """
                mutation($userId : String!){
                        editUser(editUserInput: {
                          id : $userId
                          name: "name1 updated"
                          password: "1234"
                        })
                        {
                          id
                          name
                          password
                        }
                      }
                """;

        graphQlTester.document(document)
                .variable("userId", userEntity.getId())
                .execute()
                .path("editUser")
                .entity(User.class)
                .satisfies(user -> {
                    assertThat(user.getName()).isEqualTo("name1 updated");
                    assertThat(user.getPassword()).isNotEqualTo(userEntity.getPassword());
                });
    }

    @Test
    @WithMockUser(username = "user3")
    void testEditUser_withWrongUser_giveWrongUser() {
        User userEntity = userRepository.findUserByUsername("user1");
        //language=GraphQL
        String document = """
                mutation($userId : String!){
                        editUser(editUserInput: {
                          id : $userId
                          name: "name1 updated"
                          password: "1234"
                        })
                        {
                          id
                          name
                          password
                        }
                      }
                """;

        graphQlTester.document(document)
                .variable("userId", userEntity.getId())
                .execute()
                .errors()
                .satisfy(responseErrors -> assertThat(responseErrors.get(0).getMessage()).isEqualTo("You are not the right user for this process!"))
                .path("editUser");
    }

    @Test
    void testRegisterUser() {
        //language=GraphQL
        String document = """
                mutation{
                        registerUser(
                          registerUserInput : {
                            name: "name5"
                            surname: "surname5"
                            password: "1234"
                            username: "user5"
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
                    assertThat(user.getName()).isEqualTo("name5");
                    assertThat(user.getSurname()).isEqualTo("surname5");
                    assertThat(user.getPassword()).isNotEqualTo("1234");
                    assertThat(user.getUsername()).isEqualTo("user5");
                });
    }

    @Test
    void testRegisterUser_withAlreadyExistUsername_giveUserAlreadyExistsException() {
        //language=GraphQL
        String document = """
                mutation{
                        registerUser(
                          registerUserInput : {
                            name: "name6"
                            surname: "surname6"
                            password: "1234"
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
                .errors()
                .satisfy(responseErrors -> assertThat(responseErrors.get(0).getMessage()).isEqualTo("A user already exists with this username, please try another one"))
                .path("registerUser");
    }
}