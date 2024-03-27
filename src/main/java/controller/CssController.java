package controller;

import http.HttpRequest;
import http.HttpResponse;
import java.io.IOException;

public class CssController implements Controller{
    @Override
    public HttpResponse runLogic(HttpRequest httpRequest) throws IOException {
        System.out.println("CSS CONTROLLER CALL");
        return HttpResponse.of200CssResponse("/Users/tony/IdeaProjects/KUIT3_Backend-Java-Tomcat/webapp/css/styles.css");
    }
}
