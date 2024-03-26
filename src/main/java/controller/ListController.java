package controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static webserver.UrlPath.*;

public class ListController implements Controller{
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        if (!request.getCookie().equals("logined=true")) {
            response.response302Header(LOGIN.getPath());
            return;
        }
        response.setBody(Files.readAllBytes(Paths.get(ROOT.getPath() + LIST.getPath())));
    }
}
