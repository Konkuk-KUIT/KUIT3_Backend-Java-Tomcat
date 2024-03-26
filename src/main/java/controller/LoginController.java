//package controller;
//
//import db.MemoryUserRepository;
//import http.util.HttpRequestUtils;
//import http.util.IOUtils;
//import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.Map;
//import service.UserService;
//import structure.StartLine;
//
//public class LoginController implements Controller{
//
//    UserService userService = new UserService(MemoryUserRepository.getInstance());
//
//    @Override
//    public boolean doesFit(StartLine startLine) {
//        return startLine.isMatchingPath("user/login");
//    }
//
//    private byte[] httpGetMethodLogic() throws RuntimeException {
//        try (FileInputStream input = new FileInputStream("/Users/tony/IdeaProjects/KUIT3_Backend-Java-Tomcat/webapp/user/login.html")) {    // TODO: 얘 고쳐라, Path.of() 몰라
//            return input.readAllBytes();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private byte[] httpPostMethodLogic(BufferedReader br, DataOutputStream dos) throws IOException {    // TODO: 똥코드
//        int requestContentLength = HttpRequestUtils.parseContentLength(br);
//
//        String parameterMessage = IOUtils.readData(br, requestContentLength);
//
//        Map<String, String> loginData = HttpRequestUtils.parseQueryParameter(parameterMessage);
//
//        userService.login(loginData);
//
//        try (FileInputStream input = new FileInputStream("/Users/tony/IdeaProjects/KUIT3_Backend-Java-Tomcat/webapp/index.html")) {    // TODO: status code 302 redirect 따로빼
//            return input.readAllBytes();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
