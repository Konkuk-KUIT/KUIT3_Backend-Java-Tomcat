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

public class SignUpController implements Controller{
    MemoryUserRepository db_user = getInstance();

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> user_info = parseQueryParameter(request.getBody());
        if (user_info.get("userId") != null && user_info.get("password") != null && user_info.get("name") != null && user_info.get("email") != null){
            User user = User.from(user_info.get("userId"), user_info.get("password"), user_info.get("name"), user_info.get("email"));
            db_user.addUser(user);
        }
        response.getRedirect(UrlList.INDEX_URL.getUrl());
    }
}
