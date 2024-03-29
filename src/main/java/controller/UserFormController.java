package controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.nio.file.Paths;

import static java.nio.file.Files.readAllBytes;
import static webserver.enums.UrlPath.ROOT;
import static webserver.enums.UrlPath.USER_FORM;

public class UserFormController implements Controller{

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        response.setBody(readAllBytes(Paths.get( ROOT.getPath() + USER_FORM.getPath())));
        response.response200Header();
        response.responseBody();
    }
}