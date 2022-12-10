package org.example;

import org.example.model.entity.Role;
import org.example.model.entity.Tournament;
import org.example.model.entity.User;
import org.example.repository.RoleRepository;
import org.example.repository.TournamentRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//@DataMongoTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class DBTestConfig extends RootTestConfig {

    @Autowired
    TournamentRepository tournamentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @BeforeAll
    void saveObjects() {
        Role userRole = new Role(null, "ROLE_USER");
        Role adminRole = new Role(null, "ROLE_ADMIN");
        roleRepository.saveAll(List.of(userRole, adminRole));

        userRole = roleRepository.findByName("ROLE_USER");
        adminRole = roleRepository.findByName("ROLE_ADMIN");


        User user1 = new User(null, "name1", "surname1", "1234", "user1", null, List.of(userRole));
        User user2 = new User(null, "name2", "surname2", "1234", "adminUser", null, List.of(adminRole));
        User user3 = new User(null, "name3", "surname3", "1234", "user3", null, List.of(userRole));
        User user4 = new User(null, "name4", "surname4", "1234", "user4", null, List.of(userRole));
        userRepository.saveAll(List.of(user1, user2, user3, user4));

        Tournament tournament1 = new Tournament(null, "Name1", "Game1", BigDecimal.valueOf(100), "USD", 5, "", LocalDateTime.now(), "TR", user1.getId(), List.of(user2.getId(), user3.getId()));
        Tournament tournament2 = new Tournament(null, "Name2", "Game2", BigDecimal.valueOf(100), "USD", 5, "", LocalDateTime.now(), "TR", user2.getId(), List.of(user3.getId()));
        tournamentRepository.saveAll(List.of(tournament1, tournament2));

        user2.getTournamentId().add(tournament1.getId());
        user3.getTournamentId().addAll(List.of(tournament1.getId(), tournament2.getId()));
        userRepository.saveAll(List.of(user2, user3));
    }

    @AfterAll
    void removeObjects(){
        roleRepository.deleteAll();
        tournamentRepository.deleteAll();
        userRepository.deleteAll();
    }


}
