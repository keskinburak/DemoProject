package org.example.integration.controller;

import org.example.DBTestConfig;
import org.example.model.entity.Tournament;
import org.example.repository.TournamentRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureGraphQlTester
public class TournamentControllerIntTest extends DBTestConfig {

    @Autowired
    TournamentRepository tournamentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GraphQlTester graphQlTester;

    public static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

    @Test
    void testTournaments(){

        //language=GraphQL
        String document = """
        query {
        tournaments {
                id
                name
                game
                ownerId
                userList
                }
        }
        """;

        graphQlTester.document(document)
                .execute()
                .path("tournaments")
                .entityList(Tournament.class)
                .satisfies(tournaments -> assertThat(tournaments.size()).isGreaterThanOrEqualTo(2));
    }

    @Test
    @WithMockUser( username = "user3")
    void testJoinedTournaments_withUser(){
        //language=GraphQL
        String document = """
        query {
                 joinedTournaments {
                     id
                     game
                 }
             }
        """;

        graphQlTester.document(document)
                .execute()
                .path("joinedTournaments")
                .entityList(Tournament.class)
                .satisfies(tournaments -> assertThat(tournaments.size()).isGreaterThanOrEqualTo(1));
    }

    @Test
    void testJoinedTournaments_withoutUser_giveUnauthorized(){
        //language=GraphQL
        String document = """
        query {
                 joinedTournaments {
                     id
                     game
                 }
             }
        """;

        graphQlTester.document(document)
                .execute()
                .errors()
                .satisfy(responseErrors -> {
                    assertThat(responseErrors).isNotEmpty();
                    assertEquals(responseErrors.get(0).getMessage(), "Unauthorized");
                })
                .path("joinedTournaments");
    }

    @Test
    @WithMockUser( username = "user1")
    void testCreatedTournaments_withUser(){
        //language=GraphQL
        String document = """
        query {
                createdTournaments {
                  id
                  name
                  game
                  prize
                  currency
                  teamSize
                  bracketType
                  dateTime
                  region
                  ownerId
                  userList
                }
              }
        """;

        graphQlTester.document(document)
                .execute()
                .path("createdTournaments")
                .entityList(Tournament.class)
                .satisfies(tournaments -> assertThat(tournaments.size()).isGreaterThan(1));
    }

    @Test
    @WithMockUser( username = "user1")
    void testCreateTournament(){
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime.plus(5, ChronoUnit.DAYS);
        String localDateTimeString = localDateTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT));

        //language=GraphQL
        String document = """
        mutation($dateTime : String){
                  createTournament
                  (createTournamentInput:
                    {name: "test mutation name"
                      game: "test mutation game"
                      prize: "50.50"
                      currency: "TRY"
                      teamSize: 5
                      bracketType: "Battle Royal"
                      dateTime: $dateTime
                      region: "Europe"
                    })
                  {
                    id
                    name
                    game
                    dateTime
                  }
                }
        """;

        graphQlTester.document(document)
                .variable("dateTime", localDateTimeString)
                .execute()
                .path("createTournament")
                .entity(Tournament.class)
                .satisfies(tournaments -> {
                    assertThat(tournaments.getName()).isEqualTo("test mutation name");
                    assertThat(tournaments.getId()).isNotNull();
                    assertThat(tournaments.getDateTime()).isEqualTo(LocalDateTime.parse(localDateTimeString, DateTimeFormatter.ofPattern(DATE_FORMAT)));
                });
    }

    @Test
    @WithMockUser( username = "user4")
    void testJoinTournament(){
        String userId = userRepository.findUserByUsername("user4").getId();
        Tournament tournament1 = tournamentRepository.findAll().stream().filter(tournament -> !tournament.getUserList().contains(userId)).findFirst().orElseThrow();

        //language=GraphQL
        String document = """
        mutation ($tournamentId : String){
          joinTournament(tournamentId: $tournamentId) {
            id
            name
            game
            userList
          }
        }
        """;

        graphQlTester.document(document)
                .variable("tournamentId", tournament1.getId())
                .execute()
                .path("joinTournament")
                .entity(Tournament.class)
                .satisfies(tournament -> assertThat(tournament.getUserList()).contains(userId));
    }

    @Test
    @WithMockUser( username = "user3")
    void testUnjoinTournament(){
        String userId = userRepository.findUserByUsername("user3").getId();
        Tournament tournament1 = tournamentRepository.findAll().stream().filter(tournament -> tournament.getUserList().contains(userId)).findFirst().orElseThrow();

        //language=GraphQL
        String document = """
        mutation ($tournamentId : String){
          unjoinTournament(tournamentId: $tournamentId) {
            id
            name
            game
            userList
          }
        }
        """;

        graphQlTester.document(document)
                .variable("tournamentId", tournament1.getId())
                .execute()
                .path("unjoinTournament")
                .entity(Tournament.class)
                .satisfies(tournament -> assertThat(tournament.getUserList()).doesNotContain(userId));
    }

    @Test
    @WithMockUser( username = "user1")
    void testEditTournament_withRightOwner(){
        String userId = userRepository.findUserByUsername("user1").getId();
        Tournament tournament1 = tournamentRepository.findAll().stream().filter(tournament -> tournament.getOwnerId().equals(userId)).findFirst().orElseThrow();

        //language=GraphQL
        String document = """
        mutation ($tournamentId : String!){
          editTournament(editTournamentInput:
          { id: $tournamentId
            name: "Test Updated Tournament Name"
          }) {
            id
            name
          }
        }
        """;

        graphQlTester.document(document)
                .variable("tournamentId", tournament1.getId())
                .execute()
                .path("editTournament")
                .entity(Tournament.class)
                .satisfies(tournament -> assertThat(tournament.getName()).isEqualTo("Test Updated Tournament Name"));
    }

    @Test
    @WithMockUser( username = "user1")
    void testEditTournament_withWrongOwner(){
        String userId = userRepository.findUserByUsername("user1").getId();
        Tournament tournament1 = tournamentRepository.findAll().stream().filter(tournament -> !tournament.getOwnerId().equals(userId)).findFirst().orElseThrow();

        //language=GraphQL
        String document = """
        mutation ($tournamentId : String!){
          editTournament(editTournamentInput:
          { id: $tournamentId
            name: "Test Updated Tournament Name"
          }) {
            id
            name
          }
        }
        """;

        graphQlTester.document(document)
                .variable("tournamentId", tournament1.getId())
                .execute()
                .errors()
                .satisfy(responseErrors -> {
                    assertThat(responseErrors).isNotEmpty();
                    assertEquals(responseErrors.get(0).getMessage(), "This tournament wasn't created by you !");
                })
                .path("editTournament");
    }

}