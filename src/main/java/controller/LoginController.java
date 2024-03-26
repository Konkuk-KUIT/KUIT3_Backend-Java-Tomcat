package controller;

import db.Repository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.util.Map;

import static http.util.HttpRequestUtils.parseQueryParameter;
import static webserver.UrlPath.INDEX;
import static webserver.UrlPath.LOGIN_FAILED;
import static webserver.UserQueryKey.PASSWORD;
import static webserver.UserQueryKey.USER_ID;

public class LoginController implements Controller{
    private final Repository repository;

    public LoginController(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String queryString = request.getBody();
        Map<String, String> queryParameter = parseQueryParameter(queryString);
        String userId = queryParameter.get(USER_ID.getKey());
        User user = repository.findUserById(userId);
        if (user != null && user.getPassword().equals(queryParameter.get(PASSWORD.getKey()))) {
            response.response302HeaderWithCookie(INDEX.getPath());
            return;
        }
        response.response302Header(LOGIN_FAILED.getPath());
    }
}
