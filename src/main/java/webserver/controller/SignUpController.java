package webserver.controller;

import db.MemoryUserRepository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.PageURL;

import java.io.IOException;
import java.util.Map;

import static http.util.HttpRequestUtils.getQueryParameter;

public class SignUpController implements Controller{

    @Override
    public void execute(HttpRequest req, HttpResponse res) throws IOException {

        Map<String, String> queryParameter = getQueryParameter(req.getQueryString());

        User user = new User(queryParameter.get("userId"), queryParameter.get("password"), queryParameter.get("name"), queryParameter.get("email"));
        MemoryUserRepository.getInstance().addUser(user);

        res.redirect(PageURL.HOME.getUrl());
    }
}
