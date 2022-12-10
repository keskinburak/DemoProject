package org.example.mapper;

import org.example.model.CreateTournamentInput;
import org.example.model.EditTournamentInput;
import org.example.model.entity.Tournament;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TournamentMapper {

    TournamentMapper INSTANCE = Mappers.getMapper(TournamentMapper.class);

    @Mapping(target = "dateTime", source = "dateTime", dateFormat = "dd-MM-yyyy HH:mm:ss")
    Tournament createTournamentInputToTournament(CreateTournamentInput createTournamentInput);

    @Mapping(target = "dateTime", source = "dateTime", dateFormat = "dd-MM-yyyy HH:mm:ss")
    default Tournament editTournamentInputToTournament(EditTournamentInput editTournamentInput, Tournament tournament) {
        if (editTournamentInput == null) {
            return tournament;
        }

        if (editTournamentInput.getBracketType() != null) {
            tournament.setDateTime(LocalDateTime.parse(editTournamentInput.getBracketType(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        }
        if (editTournamentInput.getName() != null) {
            tournament.setName(editTournamentInput.getName());
        }
        if (editTournamentInput.getGame() != null) {
            tournament.setGame(editTournamentInput.getGame());
        }
        if (editTournamentInput.getPrize() != null) {
            tournament.setPrize(editTournamentInput.getPrize());
        }
        if (editTournamentInput.getCurrency() != null) {
            tournament.setCurrency(editTournamentInput.getCurrency());
        }
        if (editTournamentInput.getTeamSize() != null) {
            tournament.setTeamSize(editTournamentInput.getTeamSize());
        }
        if (editTournamentInput.getBracketType() != null) {
            tournament.setBracketType(editTournamentInput.getBracketType());
        }
        if (editTournamentInput.getRegion() != null) {
            tournament.setRegion(editTournamentInput.getRegion());
        }

        return tournament;
    }

}
