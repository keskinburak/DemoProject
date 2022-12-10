package org.example.unit.service;

import org.example.error.UserAlreadyJoinException;
import org.example.error.UserDidNotJoinException;
import org.example.error.WrongUserException;
import org.example.mapper.TournamentMapper;
import org.example.model.CreateTournamentInput;
import org.example.model.entity.Tournament;
import org.example.model.entity.User;
import org.example.repository.TournamentRepository;
import org.example.service.TournamentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TournamentServiceUnitTest {

    @Mock
    TournamentRepository tournamentRepository;

    @InjectMocks
    TournamentService tournamentService;

    @Mock
    TournamentMapper tournamentMapper;

    public static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

    @Test
    void givenTournament_whenSaveTournament_thenReturnTournament() {
        Tournament returnTournament = new Tournament();
        returnTournament.setId("1");
        returnTournament.setName("Unit Test Tournament");

        doReturn(returnTournament).when(tournamentRepository).save(returnTournament);

        Tournament savedTournament = tournamentService.saveTournament(returnTournament);

        assertThat(savedTournament.getName()).isEqualTo("Unit Test Tournament");
    }

    @Test
    void givenTournamentId_whenGetTournament_thenReturnTournament() {
        Tournament returnTournament = new Tournament();
        returnTournament.setId("1");
        returnTournament.setName("Unit Test Tournament");

        doReturn(Optional.of(returnTournament)).when(tournamentRepository).findById("1");


        Tournament returnedTournament = tournamentService.getTournament("1");

        assertThat(returnedTournament.getName()).isEqualTo("Unit Test Tournament");
    }

    @Test
    void givenTournamentId_whenGetTournament_thenThrowException() {
        Tournament returnTournament = new Tournament();
        returnTournament.setId("1");
        returnTournament.setName("Unit Test Tournament");

        doThrow(new NoSuchElementException()).when(tournamentRepository).findById("1");

        assertThrows(NoSuchElementException.class, () -> {
            tournamentService.getTournament("1");
        });

        verify(tournamentRepository, times(1)).findById("1");
    }

    @Test
    void whenGetTournaments_thenReturnTournamentList() {
        Tournament tournament1 = new Tournament();
        tournament1.setId("1");
        tournament1.setName("tournament1");
        tournament1.setGame("game1");
        Tournament tournament2 = new Tournament();
        tournament2.setId("2");
        tournament2.setName("tournament2");
        tournament2.setGame("game2");

        doReturn(List.of(tournament1, tournament2)).when(tournamentRepository).findAll();

        List<Tournament> returnedTournaments = tournamentService.getTournaments();

        assertThat(returnedTournaments.size()).isEqualTo(2);
    }

    @Test
    void givenUserId_whenJoinedTournaments_thenReturnTournamentList() {
        Tournament tournament1 = new Tournament();
        tournament1.setId("1");
        tournament1.setName("tournament1");
        tournament1.setGame("game1");
        tournament1.setOwnerId("1");
        Tournament tournament2 = new Tournament();
        tournament2.setId("2");
        tournament2.setName("tournament2");
        tournament2.setGame("game2");
        tournament2.setOwnerId("1");

        doReturn(List.of(tournament1, tournament2)).when(tournamentRepository).findByUserListContaining("1");

        List<Tournament> returnedTournaments = tournamentService.joinedTournaments("1");

        assertThat(returnedTournaments.size()).isEqualTo(2);
    }

    @Test
    void givenUserId_whenCreatedTournaments_thenReturnTournamentList() {
        Tournament tournament1 = new Tournament();
        tournament1.setId("1");
        tournament1.setName("tournament1");
        tournament1.setGame("game1");
        tournament1.setOwnerId("1");
        Tournament tournament2 = new Tournament();
        tournament2.setId("2");
        tournament2.setName("tournament2");
        tournament2.setGame("game2");
        tournament2.setOwnerId("1");

        doReturn(List.of(tournament1, tournament2)).when(tournamentRepository).findByOwnerId("1");

        List<Tournament> returnedTournaments = tournamentService.createdTournaments("1");

        assertThat(returnedTournaments.size()).isEqualTo(2);
    }

    @Test
    void givenCreateTournamentInput_AndUserId_whenCreateTournament_thenReturnTournament() {
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime.plus(5, ChronoUnit.DAYS);
        String localDateTimeString = localDateTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT));

        CreateTournamentInput createTournamentInput = new CreateTournamentInput();
        createTournamentInput.setName("test mutation name");
        createTournamentInput.setGame("test mutation game");
        createTournamentInput.setPrize(BigDecimal.valueOf(50.50));
        createTournamentInput.setCurrency("TRY");
        createTournamentInput.setRegion("Europe");
        createTournamentInput.setBracketType("Battle Royal");
        createTournamentInput.setTeamSize(5);
        createTournamentInput.setDateTime(localDateTimeString);

        Tournament tournament1 = new Tournament();
        tournament1.setName("test mutation name");
        tournament1.setGame("test mutation game");
        tournament1.setPrize(BigDecimal.valueOf(50.50));
        tournament1.setCurrency("TRY");
        tournament1.setRegion("Europe");
        tournament1.setBracketType("Battle Royal");
        tournament1.setTeamSize(5);
        tournament1.setOwnerId("1");
        tournament1.setDateTime(LocalDateTime.parse(localDateTimeString, DateTimeFormatter.ofPattern(DATE_FORMAT)));

        Tournament savedTournament = new Tournament();
        savedTournament.setId("1");
        savedTournament.setName("test mutation name");
        savedTournament.setGame("test mutation game");
        savedTournament.setPrize(BigDecimal.valueOf(50.50));
        savedTournament.setCurrency("TRY");
        savedTournament.setRegion("Europe");
        savedTournament.setBracketType("Battle Royal");
        savedTournament.setTeamSize(5);
        savedTournament.setOwnerId("1");
        savedTournament.setDateTime(LocalDateTime.parse(localDateTimeString, DateTimeFormatter.ofPattern(DATE_FORMAT)));

        lenient().doReturn(tournament1).when(tournamentMapper).createTournamentInputToTournament(createTournamentInput);
        doReturn(savedTournament).when(tournamentRepository).save(tournament1);

        Tournament returnedTournaments = tournamentService.createTournament(createTournamentInput, "1");

        assertThat(returnedTournaments.getId()).isNotNull();
        assertThat(returnedTournaments.getOwnerId()).isEqualTo("1");
    }

    @Test
    void givenTournament_AndUser_whenJoinTournament_thenReturnTournament() {
        User willJoinUser = new User();
        willJoinUser.setId("2");
        willJoinUser.setUsername("user2");

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
        tournament1.getUserList().add("1");

        Tournament savedTournament = new Tournament();
        savedTournament.setId("1");
        savedTournament.setName("test mutation name");
        savedTournament.setGame("test mutation game");
        savedTournament.setPrize(BigDecimal.valueOf(50.50));
        savedTournament.setCurrency("TRY");
        savedTournament.setRegion("Europe");
        savedTournament.setBracketType("Battle Royal");
        savedTournament.setTeamSize(5);
        savedTournament.setOwnerId("2");
        savedTournament.getUserList().addAll(tournament1.getUserList());
        savedTournament.getUserList().add("2");

        doReturn(savedTournament).when(tournamentRepository).save(savedTournament);

        Tournament returnedTournaments = tournamentService.joinTournament(tournament1, willJoinUser);

        assertThat(returnedTournaments.getUserList()).contains("2");
    }

    @Test
    void givenTournament_AndAlreadyJoinedUser_whenJoinTournament_thenThrowUserAlreadyJoinException() {
        User willJoinUser = new User();
        willJoinUser.setId("2");
        willJoinUser.setUsername("user2");

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
        tournament1.setUserList(List.of("1", "2"));

        assertThrows(UserAlreadyJoinException.class, () -> {
            tournamentService.joinTournament(tournament1, willJoinUser);
        });

        verify(tournamentRepository, times(0)).save(any());
    }

    @Test
    void givenTournament_AndUser_whenUnjoinTournament_thenReturnTournament() {
        User willUnjoinUser = new User();
        willUnjoinUser.setId("2");
        willUnjoinUser.setUsername("user2");

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
        tournament1.getUserList().addAll(List.of("1", "2"));

        Tournament savedTournament = new Tournament();
        savedTournament.setId("1");
        savedTournament.setName("test mutation name");
        savedTournament.setGame("test mutation game");
        savedTournament.setPrize(BigDecimal.valueOf(50.50));
        savedTournament.setCurrency("TRY");
        savedTournament.setRegion("Europe");
        savedTournament.setBracketType("Battle Royal");
        savedTournament.setTeamSize(5);
        savedTournament.setOwnerId("2");
        savedTournament.getUserList().addAll(tournament1.getUserList());
        savedTournament.getUserList().remove("2");

        doReturn(savedTournament).when(tournamentRepository).save(savedTournament);

        Tournament returnedTournaments = tournamentService.unjoinTournament(tournament1, willUnjoinUser);

        assertThat(returnedTournaments.getUserList()).doesNotContain("2");
    }

    @Test
    void givenTournament_AndAlreadyUnjoinedUser_whenUnjoinTournament_thenThrowUserDidNotJoinException() {
        User willUnjoinUser = new User();
        willUnjoinUser.setId("2");
        willUnjoinUser.setUsername("user2");

        Tournament tournament1 = new Tournament();
        tournament1.setId("1");
        tournament1.setName("test mutation name");
        tournament1.setGame("test mutation game");
        tournament1.setPrize(BigDecimal.valueOf(50.50));
        tournament1.setCurrency("TRY");
        tournament1.setRegion("Europe");
        tournament1.setBracketType("Battle Royal");
        tournament1.setTeamSize(5);
        tournament1.setOwnerId("3");
        tournament1.setUserList(List.of("1"));

        assertThrows(UserDidNotJoinException.class, () -> {
            tournamentService.unjoinTournament(tournament1, willUnjoinUser);
        });

        verify(tournamentRepository, times(0)).save(any());
    }

    @Test
    void givenTournament_AndUserId_whenEditTournament_thenReturnTournament() {
        Tournament tournament = new Tournament();
        tournament.setId("1");
        tournament.setName("test mutation name");
        tournament.setGame("test mutation game");
        tournament.setPrize(BigDecimal.valueOf(50.50));
        tournament.setCurrency("TRY");
        tournament.setRegion("Europe");
        tournament.setBracketType("Battle Royal");
        tournament.setTeamSize(5);
        tournament.setOwnerId("2");
        tournament.setUserList(List.of("1"));

        doReturn(tournament).when(tournamentRepository).save(tournament);

        Tournament returnedTournaments = tournamentService.editTournament(tournament, "2");

        assertThat(returnedTournaments.getOwnerId()).isEqualTo("2");
        verify(tournamentRepository, times(1)).save(any());
    }

    @Test
    void givenTournament_AndUserId_whenEditTournament_thenThrowWrongUserException() {
        Tournament tournament = new Tournament();
        tournament.setId("1");
        tournament.setName("test mutation name");
        tournament.setGame("test mutation game");
        tournament.setPrize(BigDecimal.valueOf(50.50));
        tournament.setCurrency("TRY");
        tournament.setRegion("Europe");
        tournament.setBracketType("Battle Royal");
        tournament.setTeamSize(5);
        tournament.setOwnerId("3");
        tournament.setUserList(List.of("1"));

        assertThrows(WrongUserException.class, () -> {
            tournamentService.editTournament(tournament, "2");
        });

        verify(tournamentRepository, times(0)).save(any());
    }
}
