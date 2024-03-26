package controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.nio.file.Paths;

import static java.nio.file.Files.readAllBytes;
import static webserver.UrlPath.ROOT;

public class CssController implements Controller{
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        response.setBody(readAllBytes(Paths.get(ROOT.getPath() + request.getPath())));
        response.response200HeaderWithCss();
        response.responseBody();
    }
}
