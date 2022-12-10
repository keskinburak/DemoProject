package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.error.UserAlreadyJoinException;
import org.example.error.UserDidNotJoinException;
import org.example.error.WrongUserException;
import org.example.mapper.TournamentMapper;
import org.example.model.CreateTournamentInput;
import org.example.model.entity.Tournament;
import org.example.model.entity.User;
import org.example.repository.TournamentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentService {

    private final TournamentRepository tournamentRepository;

    public Tournament saveTournament(Tournament tournament) {
        log.info("Saving new Tournament {} to the database", tournament.getName());
        return tournamentRepository.save(tournament);
    }

    public Tournament getTournament(String tournamentId) {
        log.info("Get Tournament {} from database", tournamentId);
        return tournamentRepository.findById(tournamentId).orElseThrow();
    }

    public List<Tournament> getTournaments() {
        return tournamentRepository.findAll();
    }

    public List<Tournament> joinedTournaments(String userId) {
        return tournamentRepository.findByUserListContaining(userId);
    }

    public List<Tournament> createdTournaments(String userId) {
        return tournamentRepository.findByOwnerId(userId);
    }

    public Tournament createTournament(CreateTournamentInput createTournamentInput, String userId) {
        Tournament tournament = TournamentMapper.INSTANCE.createTournamentInputToTournament(createTournamentInput);
        tournament.setOwnerId(userId);
        return saveTournament(tournament);
    }

    public Tournament joinTournament(Tournament tournament, User user) {
        if (tournament.getUserList().contains(user.getId())) {
            log.error("You already joined tournament!");
            throw new UserAlreadyJoinException("You already joined tournament!", "user id");
        }
        tournament.getUserList().add(user.getId());
        return saveTournament(tournament);
    }

    public Tournament unjoinTournament(Tournament tournament, User user) {
        if (!tournament.getUserList().contains(user.getId())) {
            log.error("You didn't joined tournament!");
            throw new UserDidNotJoinException("You didn't joined tournament!", "user id");
        }
        tournament.getUserList().remove(user.getId());
        return saveTournament(tournament);
    }

    public Tournament editTournament(Tournament tournament, String userId) {
        if (!tournament.getOwnerId().equals(userId)) {
            log.error("This tournament wasn't created by you !");
            throw new WrongUserException("This tournament wasn't created by you !");
        }

        return saveTournament(tournament);
    }

}
