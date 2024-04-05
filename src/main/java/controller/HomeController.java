package controller;

import http.request.HttpRequest;
import http.request.RequestURL;
import http.response.HttpResponse;

import java.io.IOException;

public class HomeController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        response.forward(RequestURL.HOME_URL.get());
    }
}
