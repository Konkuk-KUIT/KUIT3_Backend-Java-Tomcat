package webserver.controller;

import db.MemoryUserRepository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.PageURL;

import java.io.IOException;
import java.util.Map;

import static http.util.HttpRequestUtils.getQueryParameter;

public class LoginController implements Controller{
    @Override
    public void execute(HttpRequest req, HttpResponse res) throws IOException {

        Map<String, String> queryParameter = getQueryParameter(req.getQueryString());
        User user = MemoryUserRepository.getInstance().findUserById(queryParameter.get("userId"));
        login(res, user, queryParameter);
    }

    private static void login(HttpResponse res, User user, Map<String, String> queryParameter) {
        if (user != null && user.getPassword().equals(queryParameter.get("password"))) {
            res.response302HeaderWithCookie(PageURL.HOME.getUrl());
            return;
        }
        res.redirect(PageURL.LOGIN_FAILED.getUrl());
    }
}
