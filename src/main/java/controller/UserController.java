package controller;

import http.HttpRequest;
import http.HttpResponse;
import java.io.IOException;
import structure.HeaderKey;

public class UserController implements Controller{
    @Override
    public HttpResponse runLogic(HttpRequest httpRequest) throws IOException {
        if(isLoggedIn(httpRequest)) {
            return showUserListResponse();
        }
        return redirectToLogin();
    }

    private boolean isLoggedIn(HttpRequest httpRequest) {
        String cookie = httpRequest.parseHeaderValue(HeaderKey.COOKIE);
        System.out.println(cookie);
        if(cookie != null && cookie.equals("logined=true")) {
            return true;
        };
        return false;
    }

    private HttpResponse showUserListResponse() {
        return HttpResponse.of200HtmlFile("/Users/tony/IdeaProjects/KUIT3_Backend-Java-Tomcat/webapp/user/list.html");
    }

    private HttpResponse redirectToLogin() {
        System.out.println("REDIRECTION CALLED");
        return HttpResponse.of302ResponseHeader("/user/login");
    }
}
