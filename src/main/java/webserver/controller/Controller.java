package webserver.controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;

public interface Controller {
    void execute(HttpRequest req, HttpResponse res) throws IOException;
}
