package controller;

import db.MemoryUserRepository;
import db.Repository;
import http.request.HttpRequest;
import http.request.RequestURL;
import http.response.HttpResponse;
import model.User;

import java.io.IOException;

public class SignUpController implements Controller {
    private final Repository repository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        User user = new User(request.getQueryParameter("userId"),
                request.getQueryParameter("password"),
                request.getQueryParameter("name"),
                request.getQueryParameter("email"));
        repository.addUser(user);
        response.redirect(RequestURL.HOME_URL.get());
    }
}
