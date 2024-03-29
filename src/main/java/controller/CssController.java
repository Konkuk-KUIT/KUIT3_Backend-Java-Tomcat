package controller;

import http.HttpRequest;
import http.HttpResponse;
import java.io.IOException;
import http.structure.ContentType;
import http.structure.Header;
import http.structure.HeaderKey;
import http.structure.ResponseStartLine;

public class CssController implements Controller{
    @Override
    public HttpResponse runLogic(HttpRequest httpRequest) throws IOException {
        ResponseStartLine startLine = ResponseStartLine.ofResponseCode("200");
        Header header = new Header();
        header.addAttribute(HeaderKey.CONTENT_TYPE, ContentType.CSS.getTypeValue());

        return HttpResponse.ofFile(startLine, header, "/Users/tony/IdeaProjects/KUIT3_Backend-Java-Tomcat/webapp/css/styles.css");
    }
}
