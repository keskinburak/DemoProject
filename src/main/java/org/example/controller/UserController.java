package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.UserMapper;
import org.example.model.EditUserInput;
import org.example.model.RegisterUserInput;
import org.example.model.entity.User;
import org.example.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @QueryMapping
    public String hello(){
        return "Hello";
    }

    @QueryMapping
    public List<User> getUsers(){
        return userService.getUsers();
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public User editUser(@Argument EditUserInput editUserInput, Authentication authentication){
        return userService.editUser(editUserInput, authentication.getName());
    }

    @MutationMapping
    public User registerUser(@Argument RegisterUserInput registerUserInput){
        return userService.createUser(UserMapper.INSTANCE.registerUserInputToUser(registerUserInput));
    }

}
