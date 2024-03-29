package model.repository;

import java.util.Optional;
import model.entity.User;

import java.util.Collection;

public interface Repository {

  void addUser(User user);

  Optional<User> findUserById(String id);

  Collection<User> findAll();
}
