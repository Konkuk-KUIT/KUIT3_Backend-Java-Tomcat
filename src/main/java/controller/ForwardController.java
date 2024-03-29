package controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static webserver.enums.UrlPath.ROOT;

public class ForwardController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String url = request.getPath();
        Path path = Paths.get(ROOT.getPath() + url);
        response.setBody(Files.readAllBytes(path));
        response.forward(url);
        response.responseBody();
    }
}
