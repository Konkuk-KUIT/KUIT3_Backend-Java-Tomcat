package http.util.controller;

import http.util.request.HttpRequest;
import http.util.request.UrlList;
import http.util.response.HttpResponse;

import java.io.IOException;

public class UserListController implements Controller{
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        if(request.isLoginCookie()){
            response.getForward(UrlList.BASIC_URL.getUrl() + UrlList.USERLIST_URL.getUrl());
        } else {
            response.getRedirect(UrlList.LOGIN_URL.getUrl());
        }

    }
}
