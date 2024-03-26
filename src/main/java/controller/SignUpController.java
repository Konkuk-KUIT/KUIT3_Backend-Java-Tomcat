package controller;


import db.Repository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.util.Map;

import static http.util.HttpRequestUtils.parseQueryParameter;
import static webserver.UrlPath.INDEX;
import static webserver.UserQueryKey.*;

public class SignUpController implements Controller{

    private final Repository repository; // Repository 필드 추가

    public SignUpController(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String queryString = request.getBody();
        Map<String, String> queryParameter = parseQueryParameter(queryString);

        String userId = queryParameter.get(USER_ID.getKey());
        String password = queryParameter.get(PASSWORD.getKey());
        String name = queryParameter.get(NAME.getKey());
        String email = queryParameter.get(EMAIL.getKey());

        User user = new User(userId, password, name, email);
        repository.addUser(user);
        response.response302Header(INDEX.getPath());
    }
}
