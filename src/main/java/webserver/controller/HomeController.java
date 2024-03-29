package webserver.controller;

import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.PageURL;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HomeController implements Controller{

    @Override
    public void execute(HttpRequest req, HttpResponse res) throws IOException {
        res.forward(PageURL.HOME.getFullPath());
    }
}
