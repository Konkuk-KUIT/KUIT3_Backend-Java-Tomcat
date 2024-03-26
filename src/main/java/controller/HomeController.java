package controller;

import http.HttpRequest;
import http.HttpResponse;

public class HomeController implements Controller {     // Get에만 응답하는 친구 입니다.

    @Override
    public HttpResponse runLogic(HttpRequest httpRequest) {
        return HttpResponse.of200HtmlFile("/Users/tony/IdeaProjects/KUIT3_Backend-Java-Tomcat/webapp/index.html");
    }
}
