package controller;

import http.HttpRequest;
import http.HttpResponse;
import java.io.IOException;
import http.structure.ContentType;
import http.structure.Header;
import http.structure.HeaderKey;
import http.structure.ResponseStartLine;

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
        if(cookie != null && cookie.equals("login=true")) {
            return true;
        };
        return false;
    }

    private HttpResponse showUserListResponse() {
        ResponseStartLine startLine = ResponseStartLine.ofResponseCode("200");
        Header header = new Header();
        header.addAttribute(HeaderKey.CONTENT_TYPE, ContentType.HTML.getTypeValue());

        return HttpResponse.ofFile(startLine, header, "/Users/tony/IdeaProjects/KUIT3_Backend-Java-Tomcat/webapp/user/list.html");
    }

    private HttpResponse redirectToLogin() {
        ResponseStartLine startLine = ResponseStartLine.ofResponseCode("302");
        Header header = new Header();

        return HttpResponse.ofPath(startLine, header, "/user/login");
    }
}
