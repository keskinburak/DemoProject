package org.example.repository;

import org.example.model.entity.Tournament;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TournamentRepository extends MongoRepository<Tournament, String> {

    List<Tournament> findByUserListContaining(String userId);
    List<Tournament> findByOwnerId(String ownerId);

}
