package controller;

import http.request.HttpRequest;
import http.request.RequestURL;
import http.response.HttpResponse;

import java.io.IOException;

public class ListController implements Controller {

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        // 비로그인 상태 : redirect to /user/login.html
        String cookie = request.getField("Cookie");
        if (cookie == null || !cookie.equals("logined=true")) {
            response.redirect(RequestURL.LOGIN_URL.get());
            return;
        }
        // 로그인 상태 : user/list.html 반환
        response.forward(RequestURL.LIST_URL.get());
    }
}
