package webserver.controller;

import webserver.httprequest.HttpRequest;
import webserver.httpresponse.HttpResponse;

import java.io.IOException;

public interface Controller {
    void excute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;
}
