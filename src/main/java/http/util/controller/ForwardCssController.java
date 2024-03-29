package http.util.controller;

import http.util.request.HttpRequest;
import http.util.request.UrlList;
import http.util.response.HttpResponse;

import java.io.IOException;

public class ForwardCssController implements Controller{
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        response.getForwardWithCss(UrlList.BASIC_URL + request.getUrl());

    }
}
