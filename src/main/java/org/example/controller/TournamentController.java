package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.TournamentMapper;
import org.example.model.CreateTournamentInput;
import org.example.model.EditTournamentInput;
import org.example.model.entity.Tournament;
import org.example.model.entity.User;
import org.example.service.TournamentService;
import org.example.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
@Component
@RequiredArgsConstructor
@Slf4j
public class TournamentController {
    private final TournamentService tournamentService;

    private final UserService userService;


    @QueryMapping
    List<Tournament> tournaments() {
        return tournamentService.getTournaments();
    }

    //
    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    List<Tournament> joinedTournaments(Authentication authentication) {
        return tournamentService.joinedTournaments(userService.getUser(authentication.getName()).getId());
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    List<Tournament> createdTournaments(Authentication authentication) {
        return tournamentService.createdTournaments(userService.getUser(authentication.getName()).getId());
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Tournament createTournament(@Argument CreateTournamentInput createTournamentInput, Authentication authentication) {
        User user = userService.getUser(authentication.getName());
        Tournament tournamentEntity = tournamentService.createTournament(createTournamentInput, user.getId());
        userService.addTournamentIdToUser(tournamentEntity, user);
        return tournamentEntity;
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public Tournament joinTournament(@Argument String tournamentId, Authentication authentication) {
        User user = userService.getUser(authentication.getName());
        Tournament tournament = tournamentService.joinTournament(tournamentService.getTournament(tournamentId), user);
        userService.addTournamentIdToUser(tournament, user);
        return tournament;
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Tournament unjoinTournament(@Argument String tournamentId, Authentication authentication) {
        User user = userService.getUser(authentication.getName());
        Tournament tournament = tournamentService.unjoinTournament(tournamentService.getTournament(tournamentId), user);
        userService.removeTournamentIdFromUser(tournament, user);

        return tournament;
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Tournament editTournament(@Argument EditTournamentInput editTournamentInput, Authentication authentication) {
        return tournamentService
                .editTournament
                        (TournamentMapper.INSTANCE
                                        .editTournamentInputToTournament(editTournamentInput, tournamentService.getTournament(editTournamentInput.getId())),
                                userService.getUser(authentication.getName()).getId());
    }
}
