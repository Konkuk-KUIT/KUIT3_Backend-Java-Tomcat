package controller;

import db.MemoryUserRepository;
import db.Repository;
import http.request.HttpRequest;
import http.request.RequestURL;
import http.response.HttpResponse;
import model.User;

import java.io.IOException;

public class LoginController implements Controller {
    private final Repository repository = MemoryUserRepository.getInstance();
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        User user = repository.findUserById(request.getQueryParameter("userId"));

        // 로그인 성공
        if (user != null && user.getPassword().equals(request.getQueryParameter("password"))) {
            response.redirectWithCookie(RequestURL.HOME_URL.get());
            return;
        }
        // 로그인 실패
        response.redirect(RequestURL.LOGIN_FAILED_URL.get());
    }
}
