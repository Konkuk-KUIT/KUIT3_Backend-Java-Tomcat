package service;

import db.Repository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import model.User;

public class UserService {
    private final Repository repository;

    public UserService(Repository repository) {
        this.repository = repository;
    }

    public void signUpUser(Map<String, String> signUpData) {  // TODO: 시간나면 DTO만들어서 DTO에서 validation 시켜 (인자의 개수, 맞는 인자가 있는지) 여기서는 validation 이 행해지고 있지 ㅇ낳음
        User user = new User(signUpData.get("userId"), signUpData.get("password"), signUpData.get("name"),
                signUpData.get("email"));
        repository.addUser(user);
    }

    public Optional<User> login(Map<String, String> loginData) {
        Optional<User> user = repository.findUserById(loginData.get("userId"));
        if(user.isEmpty() || !Objects.equals(user.get().getPassword(), loginData.get("password"))) {
            return Optional.empty();
        }
        return user;
    }

    public List<User> getAllUsers() {
        return (List<User>) repository.findAll();
    }
}
