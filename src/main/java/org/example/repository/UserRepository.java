package org.example.repository;

import org.example.model.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    User findUserByUsername(String username);
    User findUserById(String id);
}
