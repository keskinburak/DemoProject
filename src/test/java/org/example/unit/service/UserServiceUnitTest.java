package org.example.unit.service;

import org.example.error.UserAlreadyExistsException;
import org.example.error.WrongUserException;
import org.example.mapper.UserMapper;
import org.example.model.EditUserInput;
import org.example.model.entity.Role;
import org.example.model.entity.Tournament;
import org.example.model.entity.User;
import org.example.repository.RoleRepository;
import org.example.repository.UserRepository;
import org.example.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserMapper userMapper;

    @Test
    void givenUser_whenSaveOrUpdateUser_thenReturnUser() {
        User user = new User();
        user.setId("1");
        user.setUsername("user1");

        doReturn(user).when(userRepository).save(user);

        User savedUser = userService.saveOrUpdate(user);

        assertThat(savedUser.getUsername()).isEqualTo("user1");
    }

    @Test
    void givenUser_whenCreateUser_thenReturnUser() {
        User user = new User();
        user.setId("1");
        user.setUsername("user1");
        user.setPassword("Password");

        Role userRole = new Role("1", "ROLE_USER");

        User willSaveUser = new User();
        willSaveUser.setId("1");
        willSaveUser.setUsername("user1");
        willSaveUser.setPassword("Encrypted Password");
        willSaveUser.getRoleList().add(userRole);


        doReturn("Encrypted Password").when(passwordEncoder).encode(user.getPassword());
        doReturn(null).when(userRepository).findUserByUsername(user.getUsername());
        doReturn(userRole).when(roleRepository).findByName("ROLE_USER");
        doReturn(willSaveUser).when(userRepository).save(willSaveUser);

        User returnedUser = userService.createUser(user);

        assertThat(returnedUser.getPassword()).isEqualTo("Encrypted Password");
        verify(userRepository, times(1)).findUserByUsername(user.getUsername());
    }

    @Test
    void givenAlreadyExistUser_whenCreateUser_thenThrowUserAlreadyExistsException() {
        User user = new User();
        user.setId("1");
        user.setUsername("user1");
        user.setPassword("Password");

        doReturn(user).when(userRepository).findUserByUsername(user.getUsername());

        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.createUser(user);
        });

        verify(userRepository, times(1)).findUserByUsername(user.getUsername());
        verify(passwordEncoder, times(0)).encode(user.getPassword());
        verify(roleRepository, times(0)).findByName("ROLE_USER");
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void givenUsername_whenGetUser_thenReturnUser() {
        User user = new User();
        user.setId("1");
        user.setUsername("user1");
        user.setName("user");

        doReturn(user).when(userRepository).findUserByUsername("user1");

        User returnedUser = userService.getUser("user1");

        assertThat(returnedUser.getUsername()).isEqualTo("user1");
        assertThat(returnedUser.getName()).isEqualTo("user");
    }

    @Test
    void givenUserId_whenGetUserById_thenReturnUser() {
        User user = new User();
        user.setId("1");
        user.setUsername("user1");
        user.setName("user");

        doReturn(user).when(userRepository).findUserById("1");

        User returnedUser = userService.getUserById("1");

        assertThat(returnedUser.getUsername()).isEqualTo("user1");
        assertThat(returnedUser.getName()).isEqualTo("user");
    }

    @Test
    void whenGetUsers_thenReturnUserList() {
        User user1 = new User();
        user1.setId("1");
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId("2");
        user2.setUsername("user2");

        doReturn(List.of(user1, user2)).when(userRepository).findAll();

        List<User> returnedUsers = userService.getUsers();

        assertThat(returnedUsers.size()).isEqualTo(2);
    }

    @Test
    void givenUsername_whenLoadUserByUsername_thenReturnUserDetails() {
        Role userRole = new Role("1", "ROLE_USER");
        Role adminRole = new Role("2", "ROLE_ADMIN");

        User user = new User();
        user.setId("1");
        user.setUsername("user1");
        user.setName("user");
        user.setPassword("Password");
        user.getRoleList().addAll(List.of(userRole, adminRole));

        doReturn(user).when(userRepository).findUserByUsername("user1");

        UserDetails userDetails = userService.loadUserByUsername("user1");

        assertThat(userDetails.getUsername()).isEqualTo("user1");
        assertThat(userDetails.getPassword()).isEqualTo("Password");
        assertThat(userDetails.getAuthorities().size()).isEqualTo(2);

    }

    @Test
    void givenUsername_whenLoadUserByUsername_thenThrowUsernameNotFoundException() {
        doReturn(null).when(userRepository).findUserByUsername("username");

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("username");
        });

        verify(userRepository, times(1)).findUserByUsername("username");
    }

    @Test
    void givenPassword_whenPasswordEncode_thenReturnEncodedPassword() {
        doReturn("Encoded Password").when(passwordEncoder).encode("password");

        String encodedPassword = userService.passwordEncode("password");

        assertThat(encodedPassword).isEqualTo("Encoded Password");
    }

    @Test
    void givenTournament_andUser_whenAddTournamentIdToUser() {
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

        User user = new User();
        user.setId("1");
        user.setUsername("user1");

        User willSaveUser = new User();
        willSaveUser.setId("1");
        willSaveUser.setUsername("user1");
        willSaveUser.getTournamentId().add(tournament.getId());

        doReturn(willSaveUser).when(userRepository).save(willSaveUser);

        userService.addTournamentIdToUser(tournament, user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void givenTournament_andUser_whenRemoveTournamentIdToUser() {
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

        User user = new User();
        user.setId("1");
        user.setUsername("user1");
        user.getTournamentId().add(tournament.getId());

        User willSaveUser = new User();
        willSaveUser.setId("1");
        willSaveUser.setUsername("user1");

        doReturn(willSaveUser).when(userRepository).save(willSaveUser);

        userService.removeTournamentIdFromUser(tournament, user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void givenTournament_andUser_whenEditUser_thenReturnUser() {
        EditUserInput editUserInput = new EditUserInput();
        editUserInput.setId("1");
        editUserInput.setUsername("user1");
        editUserInput.setPassword("password");

        EditUserInput passwordEncodedEditUserInput = new EditUserInput();
        passwordEncodedEditUserInput.setId("1");
        passwordEncodedEditUserInput.setUsername("user1");
        passwordEncodedEditUserInput.setPassword("Encoded Password");

        User returnedUser = new User();
        returnedUser.setId("1");
        returnedUser.setUsername("user1");
        returnedUser.setPassword("password");


        User willUpdateUser = new User();
        willUpdateUser.setId("1");
        willUpdateUser.setUsername("user1");
        willUpdateUser.setPassword("Encoded Password");

        doReturn("Encoded Password").when(passwordEncoder).encode("password");
        doReturn(returnedUser).when(userRepository).findUserById(passwordEncodedEditUserInput.getId());
        lenient().doReturn(willUpdateUser).when(userMapper).editUserInputToUser(passwordEncodedEditUserInput, returnedUser);
        doReturn(willUpdateUser).when(userRepository).save(willUpdateUser);

        userService.editUser(editUserInput, "user1");

        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).findUserById("1");
        verify(userRepository, times(1)).save(willUpdateUser);
    }

    @Test
    void givenTournament_andUser_whenEditUser_thenThrowWrongUserException() {
        EditUserInput editUserInput = new EditUserInput();
        editUserInput.setId("1");
        editUserInput.setUsername("user1");
        editUserInput.setPassword("password");

        EditUserInput returnedUser = new EditUserInput();
        returnedUser.setId("2");
        returnedUser.setUsername("user2");
        returnedUser.setPassword("password");

        User willUpdateUser = new User();
        willUpdateUser.setId("1");
        willUpdateUser.setUsername("user1");
        willUpdateUser.setPassword("password");

        doReturn("Encoded Password").when(passwordEncoder).encode("password");
        doReturn(willUpdateUser).when(userRepository).findUserById("1");

        assertThrows(WrongUserException.class, () -> {
            userService.editUser(editUserInput, "user2");
        });


        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).findUserById("1");
        verify(userMapper, times(0)).editUserInputToUser(editUserInput, willUpdateUser);
        verify(userRepository, times(0)).save(willUpdateUser);
    }

}
