package http.util.controller;

import http.util.request.HttpRequest;
import http.util.request.UrlList;
import http.util.response.HttpResponse;

import java.io.IOException;

public class ForwardController implements Controller{
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        response.getForward(UrlList.BASIC_URL + request.getUrl());

    }
}
