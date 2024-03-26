package controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.nio.file.Files;

import static webserver.UrlPath.getHomePath;

public class HomeController implements Controller{
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        response.setBody(Files.readAllBytes(getHomePath()));;
        response.response200Header();
        response.responseBody();
    }
}
