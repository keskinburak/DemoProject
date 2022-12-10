package org.example.mapper;

import org.example.model.EditUserInput;
import org.example.model.RegisterUserInput;
import org.example.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User registerUserInputToUser(RegisterUserInput registerUserInput);

    default User editUserInputToUser(EditUserInput editUserInput, User user) {
        if (editUserInput == null) {
            return user;
        }

        if (editUserInput.getUsername() != null) {
            user.setUsername(editUserInput.getUsername());
        }
        if (editUserInput.getName() != null) {
            user.setName(editUserInput.getName());
        }
        if (editUserInput.getSurname() != null) {
            user.setSurname(editUserInput.getSurname());
        }
        if (editUserInput.getPassword() != null) {
            user.setPassword(editUserInput.getPassword());
        }

        return user;
    }

}
