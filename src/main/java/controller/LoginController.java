package controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import service.UserService;

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
        if(isLoggedInFalse(httpRequest)) {
            return HttpResponse.of200HtmlFile("/Users/tony/IdeaProjects/KUIT3_Backend-Java-Tomcat/webapp/user/login_failed.html");
        }
        return HttpResponse.of200HtmlFile("/Users/tony/IdeaProjects/KUIT3_Backend-Java-Tomcat/webapp/user/login.html");
    }

    private HttpResponse httpPostMethodLogic(HttpRequest httpRequest) throws IOException {    // TODO: 똥코드
        Map<String, String> loginData = httpRequest.parseBodyQueryParameter();

        if(userService.login(loginData).isPresent()) {
            System.out.println("Login Successful");
            return HttpResponse.of302ResponseHeaderWithCookie("/");
        }
        System.out.println("Login failed");
        return HttpResponse.of302ResponseHeader("/user/login?loggedIn=false");
    }

    private boolean isLoggedInFalse(HttpRequest httpRequest) {
        Map<String, String> queryStringMap = httpRequest.getQueryStringMap();

        return Objects.equals(queryStringMap.get("loggedIn"), "false");
    }
}
