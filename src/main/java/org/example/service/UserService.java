package org.example.service;

import org.example.model.EditUserInput;
import org.example.model.entity.Tournament;
import org.example.model.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    User saveOrUpdate(User user);
    User createUser(User user);
    User getUser(String userName);
    User getUserById(String userId);
    List<User> getUsers();
    String passwordEncode(String password);

    void addTournamentIdToUser(Tournament tournament, User user);

    void removeTournamentIdFromUser(Tournament tournament, User user);

    User editUser(EditUserInput editUserInput, String authenticatedUsername);
}
