package http.util.controller;

import db.MemoryUserRepository;
import http.util.request.HttpRequest;
import http.util.request.UrlList;
import http.util.response.HttpResponse;
import model.User;

import java.io.IOException;
import java.util.Map;

import static db.MemoryUserRepository.getInstance;
import static http.util.HttpRequestUtils.parseQueryParameter;
import static http.util.IOUtils.readData;

public class LoginController implements Controller{
    MemoryUserRepository db_user = getInstance();

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> user_info = parseQueryParameter(request.getBody());
        User user = db_user.findUserById(user_info.get("userId"));

        if (user != null && user.getPassword().equals(user_info.get("password"))) {
            response.getRedirectWithCookie(UrlList.INDEX_URL.getUrl());
        } else {
            response.getRedirect(UrlList.LOGIN_FAILED_URL.getUrl());
        }
    }
}
