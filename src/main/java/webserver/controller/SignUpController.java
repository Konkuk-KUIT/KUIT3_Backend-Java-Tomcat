package webserver.controller;

import db.MemoryUserRepository;
import http.util.IOUtils;
import model.User;
import webserver.httprequest.HttpRequest;
import webserver.httpresponse.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

import static http.util.HttpRequestUtils.parseQueryParameter;
import static java.nio.file.Files.readAllBytes;
import static webserver.httprequest.UrlPath.INDEX;
import static webserver.httprequest.UrlPath.ROOT;

public class SignUpController implements Controller{
    @Override
    public void excute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        Map<String,String> m = httpRequest.getBody();
        User user = new User(m.get("userId"), m.get("password"), m.get("name"), m.get("email"));
        MemoryUserRepository.getInstance().addUser(user);
        httpResponse.response302Header(INDEX.getPath());
    }
}
