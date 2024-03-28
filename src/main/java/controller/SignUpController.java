package controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import java.io.IOException;
import java.util.Map;
import service.UserService;
import structure.ContentType;
import structure.Header;
import structure.HeaderKey;
import structure.ResponseStartLine;

public class SignUpController implements Controller {   // GET, POST 둘다 응답하는 친구 입니다.

    private final UserService userService = new UserService(MemoryUserRepository.getInstance());        // TODO: Singleton 해라

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

    private HttpResponse httpPostMethodLogic(HttpRequest httpRequest) throws IOException, IllegalArgumentException {    // TODO: 똥코드
        Map<String, String> signUpData = httpRequest.parseBodyQueryParameter();

        userService.signUpUser(signUpData);

        ResponseStartLine startLine = ResponseStartLine.ofResponseCode("302");
        Header header = new Header();
        return HttpResponse.ofPath(startLine, header, "/");
    }
}
