package controller;

import http.HttpRequest;
import http.HttpResponse;
import structure.Header;
import structure.HeaderKey;
import structure.ResponseStartLine;

public class HomeController implements Controller {     // Get에만 응답하는 친구 입니다.

    @Override
    public HttpResponse runLogic(HttpRequest httpRequest) {
        ResponseStartLine startLine = ResponseStartLine.ofResponseCode("200");
        Header header = new Header();
        header.addAttribute(HeaderKey.CONTENT_TYPE, "text/html;charset=utf-8");
        return HttpResponse.ofFile(startLine, header, "/Users/tony/IdeaProjects/KUIT3_Backend-Java-Tomcat/webapp/index.html");
    }
}
