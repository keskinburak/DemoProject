package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.error.UserAlreadyExistsException;
import org.example.error.WrongUserException;
import org.example.mapper.UserMapper;
import org.example.model.EditUserInput;
import org.example.model.entity.Tournament;
import org.example.model.entity.User;
import org.example.repository.RoleRepository;
import org.example.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User saveOrUpdate(User user) {
        log.info("Saving or Updating User {} to the database", user.getName());
        return userRepository.save(user);
    }

    @Override
    public User createUser(User user) {
        if(userRepository.findUserByUsername(user.getUsername()) != null ) {
            throw new UserAlreadyExistsException("A user already exists with this username, please try another one");
        }
        log.info("Creating new User {} to the database", user.getName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoleList().add(roleRepository.findByName("ROLE_USER"));
        return userRepository.save(user);
    }

    @Override
    public User getUser(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User getUserById(String userId) {
        return userRepository.findUserById(userId);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if(user == null){
            log.error("User not found in DB");
            throw new UsernameNotFoundException("User not found in DB");
        }
        log.info("User found in DB {}", username);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoleList().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    public String passwordEncode(String password){
        return passwordEncoder.encode(password);
    }

    @Override
    public void addTournamentIdToUser(Tournament tournament, User user) {
        user.getTournamentId().add(tournament.getId());
        saveOrUpdate(user);
    }

    @Override
    public void removeTournamentIdFromUser(Tournament tournament, User user) {
        user.getTournamentId().remove(tournament.getId());
        saveOrUpdate(user);
    }

    @Override
    public User editUser(EditUserInput editUserInput, String authenticatedUsername) {
        if (editUserInput != null && !ObjectUtils.isEmpty(editUserInput.getPassword())) editUserInput.setPassword(passwordEncode(editUserInput.getPassword()));
        assert editUserInput != null;
        User user = getUserById(editUserInput.getId());
        if(!user.getUsername().equals(authenticatedUsername)){
            throw new WrongUserException("You are not the right user for this process!");
        }
        user = UserMapper.INSTANCE.editUserInputToUser(editUserInput, user);
        return saveOrUpdate(user);
    }
}
