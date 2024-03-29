package controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.nio.file.Paths;

import static java.nio.file.Files.readAllBytes;
import static webserver.UrlPath.*;

public class ListController implements Controller{
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        if (!request.getCookie().equals("logined=true")) {
            response.response302Header(LOGIN.getPath());
            return;
        }
        String url = request.getPath();
        response.setBody(readAllBytes(Paths.get(ROOT.getPath() + "/user/list.html")));
        response.response200Header();
        response.responseBody();
    }
}
