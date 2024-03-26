package webserver.controller;

import db.MemoryUserRepository;
import model.User;
import webserver.httprequest.HttpRequest;
import webserver.httpresponse.HttpResponse;

import java.io.IOException;
import java.util.Map;

import static webserver.httprequest.UrlPath.INDEX;

public class SignUpController implements Controller{
    @Override
    public void excute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        Map<String,String> m = httpRequest.getBody();
        User user = new User(m.get("userId"), m.get("password"), m.get("name"), m.get("email"));
        MemoryUserRepository.getInstance().addUser(user);
        httpResponse.response302Header(INDEX.getPath());
    }
}
