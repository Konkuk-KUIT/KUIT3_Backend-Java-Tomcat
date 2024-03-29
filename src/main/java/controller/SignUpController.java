package controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import java.io.IOException;
import java.util.Map;
import service.UserService;
import http.structure.ContentType;
import http.structure.Header;
import http.structure.HeaderKey;
import http.structure.ResponseStartLine;

public class SignUpController implements Controller {

    private final UserService userService = new UserService(MemoryUserRepository.getInstance());

    @Override
    public HttpResponse runLogic(HttpRequest httpRequest) throws IOException {
        if(httpRequest.isGet()) {
            return httpGetMethodLogic();
        }
        if(httpRequest.isPost()) {
            return httpPostMethodLogic(httpRequest);
        }
        throw new IllegalArgumentException("지원되지 않는 HTTP method 입니다.");
    }

    private HttpResponse httpGetMethodLogic() throws RuntimeException {
        ResponseStartLine startLine = ResponseStartLine.ofResponseCode("200");
        Header header = new Header();
        header.addAttribute(HeaderKey.CONTENT_TYPE, ContentType.HTML.getTypeValue());
        return HttpResponse.ofFile(startLine, header, "/Users/tony/IdeaProjects/KUIT3_Backend-Java-Tomcat/webapp/user/form.html");
    }

    private HttpResponse httpPostMethodLogic(HttpRequest httpRequest) throws IOException, IllegalArgumentException {
        Map<String, String> signUpData = httpRequest.parseBodyQueryParameter();

        userService.signUpUser(signUpData);

        ResponseStartLine startLine = ResponseStartLine.ofResponseCode("302");
        Header header = new Header();
        return HttpResponse.ofPath(startLine, header, "/");
    }
}
