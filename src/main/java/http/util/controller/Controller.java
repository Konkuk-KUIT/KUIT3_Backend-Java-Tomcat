package http.util.controller;

import http.util.request.HttpRequest;
import http.util.response.HttpResponse;

import java.io.IOException;

public interface Controller {
    public void execute(HttpRequest request, HttpResponse response) throws IOException;
}
