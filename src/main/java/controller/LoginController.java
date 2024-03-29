package controller;

import db.MemoryUserRepository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.util.Map;

import static http.util.HttpRequestUtils.parseQueryParameter;
import static webserver.enums.UrlPath.*;
import static webserver.enums.UserQueryKey.PASSWORD;
import static webserver.enums.UserQueryKey.USER_ID;

public class LoginController implements Controller{
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String queryString = request.getBody();
        Map<String, String> queryParameter = parseQueryParameter(queryString);
        String userId = queryParameter.get(USER_ID.getKey());
        User user =  MemoryUserRepository.getInstance().findUserById(userId);
        if (user != null && user.getPassword().equals(queryParameter.get(PASSWORD.getKey()))) {
            response.response302HeaderWithCookie(INDEX.getPath());
            return;
        }
        response.response302Header(LOGIN_FAILED_HTML.getPath());
    }
}
