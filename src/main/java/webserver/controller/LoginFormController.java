package webserver.controller;

import webserver.httprequest.HttpRequest;
import webserver.httpresponse.HttpResponse;

import java.io.IOException;
import java.nio.file.Paths;

import static java.nio.file.Files.readAllBytes;
import static webserver.httprequest.UrlPath.*;

public class LoginFormController implements Controller{
    @Override
    public void excute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        httpResponse.setBody(readAllBytes(Paths.get(FILE_ROOT.getPath() + USER_LOGIN_FILE.getPath())));
        httpResponse.response200Header();
        httpResponse.responseBody();
    }
}
