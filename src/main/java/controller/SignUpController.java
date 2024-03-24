package controller;

import controller.Controller;
import db.MemoryUserRepository;
import db.Repository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import model.User;
import service.UserService;
import structure.StartLine;

public class SignUpController implements Controller {   // GET, POST 둘다 응답하는 친구 입니다.

    private final UserService userService = new UserService(MemoryUserRepository.getInstance());
    @Override
    public byte[] runLogic(BufferedReader br, DataOutputStream dos, StartLine startLine) throws IOException {
        if(startLine.isGet()) {
            return httpGetMethodLogic();
        }
        if(startLine.isPost()) {
            return httpPostMethodLogic(br, dos);
        }
        throw new IllegalArgumentException("지원되지 않는 HTTP method 입니다.");
    }

    @Override
    public boolean doesFit(StartLine startLine) {
        return startLine.isMatchingPath("/user/signup");
    }

    private byte[] httpGetMethodLogic() throws RuntimeException {
        try (FileInputStream input = new FileInputStream("/Users/tony/IdeaProjects/KUIT3_Backend-Java-Tomcat/webapp/user/form.html")) {    // TODO: 얘 고쳐라, Path.of() 몰라
            return input.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] httpPostMethodLogic(BufferedReader br, DataOutputStream dos) throws IOException {    // TODO: 똥코드
        int requestContentLength = HttpRequestUtils.parseContentLength(br);

        String parameterMessage = IOUtils.readData(br, requestContentLength);

        Map<String, String> signUpData = HttpRequestUtils.parseQueryParameter(parameterMessage);

        userService.signUpUser(signUpData);

        try (FileInputStream input = new FileInputStream("/Users/tony/IdeaProjects/KUIT3_Backend-Java-Tomcat/webapp/index.html")) {    // TODO: status code 302 redirect 따로빼
            return input.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
