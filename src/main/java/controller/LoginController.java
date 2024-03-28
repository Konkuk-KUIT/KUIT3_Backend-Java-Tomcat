package controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import service.UserService;
import structure.ContentType;
import structure.Header;
import structure.HeaderKey;
import structure.ResponseStartLine;

public class LoginController implements Controller{

    UserService userService = new UserService(MemoryUserRepository.getInstance());

    @Override
    public HttpResponse runLogic(HttpRequest httpRequest) throws IOException {
        if(httpRequest.isGet()) {
            return httpGetMethodLogic(httpRequest);
        }
        if(httpRequest.isPost()) {
            return httpPostMethodLogic(httpRequest);
        }
        throw new IllegalArgumentException("지원되지 않는 HTTP method 입니다.");
    }

    private HttpResponse httpGetMethodLogic(HttpRequest httpRequest) throws RuntimeException {
        ResponseStartLine startLine = ResponseStartLine.ofResponseCode("200");
        Header header = new Header();
        header.addAttribute(HeaderKey.CONTENT_TYPE, ContentType.HTML.getTypeValue());
        if(isLoggedInFalse(httpRequest)) {
            return HttpResponse.ofFile(startLine, header, "/Users/tony/IdeaProjects/KUIT3_Backend-Java-Tomcat/webapp/user/login_failed.html");
        }
        return HttpResponse.ofFile(startLine, header, "/Users/tony/IdeaProjects/KUIT3_Backend-Java-Tomcat/webapp/user/login.html");
    }

    private HttpResponse httpPostMethodLogic(HttpRequest httpRequest) throws IOException {    // TODO: 똥코드
        Map<String, String> loginData = httpRequest.parseBodyQueryParameter();
        ResponseStartLine responseStartLine = ResponseStartLine.ofResponseCode("302");
        Header header = new Header();
        if(userService.login(loginData).isPresent()) {
            header.addAttribute(HeaderKey.SET_COOKIE, "login=true");
            return HttpResponse.ofPath(responseStartLine, header, "/");
        }
        return HttpResponse.ofPath(responseStartLine, header, "/user/login?loggedIn=false");
    }

    private boolean isLoggedInFalse(HttpRequest httpRequest) {
        Map<String, String> queryStringMap = httpRequest.getQueryStringMap();

        return Objects.equals(queryStringMap.get("loggedIn"), "false");
    }
}
