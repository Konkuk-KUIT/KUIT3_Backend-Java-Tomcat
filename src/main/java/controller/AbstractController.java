package controller;

import http.constants.HttpMethod;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

public abstract class AbstractController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        if(request.getMethod().equals(HttpMethod.GET.get())){
            doGet(request, response);
            return;
        }
        doPost(request, response);
    }

    public abstract void doGet(HttpRequest request, HttpResponse response) throws IOException;
    public abstract void doPost(HttpRequest request, HttpResponse response) throws IOException;
}
