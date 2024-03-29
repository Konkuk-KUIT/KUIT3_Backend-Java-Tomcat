package controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.nio.file.Paths;

import static java.nio.file.Files.readAllBytes;
import static webserver.enums.UrlPath.*;

public class LoginHtmlController implements Controller{

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {

        if (request.getPath().equals(LOGIN_HTML.getPath())) {
            response.setBody(readAllBytes(Paths.get(ROOT.getPath() + LOGIN_HTML.getPath())));
        }
        if (request.getPath().equals(LOGIN_FAILED_HTML.getPath())){
            response.setBody(readAllBytes(Paths.get(ROOT.getPath() + LOGIN_FAILED_HTML.getPath())));
        }
        response.response200Header();
        response.responseBody();
    }
}
