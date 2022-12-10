package org.example.unit.controller;

import org.example.config.security.RsaKeyProperties;
import org.example.controller.TournamentController;
import org.example.model.entity.Tournament;
import org.example.model.entity.User;
import org.example.service.TournamentService;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@GraphQlTest(controllers = TournamentController.class)
public class TournamentControllerUnitTest {

    @Autowired
    GraphQlTester graphQlTester;

    @MockBean
    TournamentService tournamentService;

    @MockBean
    UserService userService;

    @MockBean
    RsaKeyProperties rsaKeyProperties;

    public static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

    @Test
    void testTournaments(){
        Tournament tournament1 = new Tournament();
        tournament1.setId("1");
        tournament1.setName("tournament1");
        tournament1.setGame("game1");
        Tournament tournament2 = new Tournament();
        tournament2.setId("2");
        tournament2.setName("tournament2");
        tournament2.setGame("game2");

        doReturn(List.of(tournament1,tournament2)).when(tournamentService).getTournaments();

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
                .satisfies(tournaments -> assertThat(tournaments.size()).isEqualTo(2));
    }

    @Test
    @WithMockUser( username = "user3")
    void testJoinedTournaments(){
        User user = new User();
        user.setId("1");
        user.setUsername("user3");

        Tournament tournament1 = new Tournament();
        tournament1.setId("1");
        tournament1.setName("tournament1");
        tournament1.setGame("game1");
        Tournament tournament2 = new Tournament();
        tournament2.setId("2");
        tournament2.setName("tournament2");
        tournament2.setGame("game2");

        doReturn(user).when(userService).getUser("user3");
        doReturn(List.of(tournament1, tournament2)).when(tournamentService).joinedTournaments(user.getId());


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
                .satisfies(tournaments -> assertThat(tournaments.size()).isEqualTo(2));
    }

    @Test
    @WithMockUser( username = "user1")
    void testCreatedTournaments(){
        User user = new User();
        user.setId("1");
        user.setUsername("user1");

        Tournament tournament1 = new Tournament();
        tournament1.setId("1");
        tournament1.setName("tournament1");
        tournament1.setGame("game1");
        tournament1.setOwnerId(user.getId());

        doReturn(user).when(userService).getUser("user1");
        doReturn(List.of(tournament1)).when(tournamentService).createdTournaments(user.getId());

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
                .satisfies(tournaments -> assertThat(tournaments.size()).isEqualTo(1));
    }

    @Test
    @WithMockUser( username = "user1")
    void testCreateTournament(){
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime.plus(5, ChronoUnit.DAYS);
        String localDateTimeString = localDateTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT));

        User user = new User();
        user.setId("1");
        user.setUsername("user1");

        Tournament tournament1 = new Tournament();
        tournament1.setId("1");
        tournament1.setName("test mutation name");
        tournament1.setGame("test mutation game");
        tournament1.setPrize(BigDecimal.valueOf(50.50));
        tournament1.setCurrency("TRY");
        tournament1.setRegion("Europe");
        tournament1.setBracketType("Battle Royal");
        tournament1.setTeamSize(5);
        tournament1.setOwnerId(user.getId());
        tournament1.setDateTime(localDateTime);

        doReturn(user).when(userService).getUser("user1");
        doReturn(tournament1).when(tournamentService).createTournament(any(), any());

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
                    assertThat(tournaments.getId()).isEqualTo("1");
                    assertThat(tournaments.getDateTime()).isEqualTo(localDateTime);
                });
    }

    @Test
    @WithMockUser( username = "user1")
    void testJoinTournament(){
        User willJoinUser = new User();
        willJoinUser.setId("1");
        willJoinUser.setUsername("user1");

        Tournament tournament1 = new Tournament();
        tournament1.setId("1");
        tournament1.setName("test mutation name");
        tournament1.setGame("test mutation game");
        tournament1.setPrize(BigDecimal.valueOf(50.50));
        tournament1.setCurrency("TRY");
        tournament1.setRegion("Europe");
        tournament1.setBracketType("Battle Royal");
        tournament1.setTeamSize(5);
        tournament1.setOwnerId("2");
        tournament1.setUserList(List.of("1"));


        doReturn(willJoinUser).when(userService).getUser("user1");
        doReturn(tournament1).when(tournamentService).getTournament("1");
        doReturn(tournament1).when(tournamentService).joinTournament(any(), any());

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
                .satisfies(tournament -> assertThat(tournament.getUserList()).contains(willJoinUser.getId()));

        verify(userService, times(1)).getUser("user1");
        verify(tournamentService, times(1)).getTournament("1");
        verify(tournamentService, times(1)).joinTournament(any(), any());
    }

    @Test
    @WithMockUser( username = "user1")
    void testUnjoinTournament(){
        User willUnjoinUser = new User();
        willUnjoinUser.setId("1");
        willUnjoinUser.setUsername("user1");

        Tournament tournament1 = new Tournament();
        tournament1.setId("1");
        tournament1.setName("test mutation name");
        tournament1.setGame("test mutation game");
        tournament1.setPrize(BigDecimal.valueOf(50.50));
        tournament1.setCurrency("TRY");
        tournament1.setRegion("Europe");
        tournament1.setBracketType("Battle Royal");
        tournament1.setTeamSize(5);
        tournament1.setOwnerId("2");
        tournament1.setUserList(List.of("3"));


        doReturn(willUnjoinUser).when(userService).getUser("user1");
        doReturn(tournament1).when(tournamentService).getTournament("1");
        doReturn(tournament1).when(tournamentService).unjoinTournament(any(), any());

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
                .satisfies(tournament -> assertThat(tournament.getUserList()).doesNotContain(willUnjoinUser.getId()));

        verify(userService, times(1)).getUser("user1");
        verify(tournamentService, times(1)).getTournament("1");
        verify(tournamentService, times(1)).unjoinTournament(any(), any());
    }

    @Test
    @WithMockUser( username = "user1")
    void testEditTournament(){
        User owner = new User();
        owner.setId("1");
        owner.setUsername("user1");

        Tournament tournament1 = new Tournament();
        tournament1.setId("1");
        tournament1.setName("Test Updated Tournament Name");
        tournament1.setGame("test mutation game");
        tournament1.setPrize(BigDecimal.valueOf(50.50));
        tournament1.setCurrency("TRY");
        tournament1.setRegion("Europe");
        tournament1.setBracketType("Battle Royal");
        tournament1.setTeamSize(5);
        tournament1.setOwnerId("2");
        tournament1.setUserList(List.of("3"));


        doReturn(owner).when(userService).getUser("user1");
        doReturn(tournament1).when(tournamentService).getTournament("1");
        doReturn(tournament1).when(tournamentService).editTournament(any(), any());

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
}
