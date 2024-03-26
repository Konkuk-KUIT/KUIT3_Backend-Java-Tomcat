//package controller;
//
//import db.MemoryUserRepository;
//import http.HttpRequest;
//import http.HttpResponse;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.Map;
//import java.util.Objects;
//import service.UserService;
//import structure.StartLine;
//
//public class SignUpController implements Controller {   // GET, POST 둘다 응답하는 친구 입니다.
//
//    private final UserService userService = new UserService(MemoryUserRepository.getInstance());        // TODO: Singleton 해라
//
//    @Override
//    public byte[] runLogic(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
//        if(httpRequest.isGet()) {
//            return httpGetMethodLogic();
//        }
//        if(httpRequest.isPost()) {
//            return httpPostMethodLogic(httpRequest, httpResponse);
//        }
//        throw new IllegalArgumentException("지원되지 않는 HTTP method 입니다.");
//    }
//
//    private byte[] httpGetMethodLogic() throws RuntimeException {
//        try (FileInputStream input = new FileInputStream("/Users/tony/IdeaProjects/KUIT3_Backend-Java-Tomcat/webapp/user/form.html")) {    // TODO: 얘 고쳐라, Path.of() 몰라
//            return input.readAllBytes();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private byte[] httpPostMethodLogic(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException, IllegalArgumentException {    // TODO: 똥코드
////        int requestContentLength = HttpRequestUtils.parseContentLength(br);
////
////        String parameterMessage = IOUtils.readData(br, requestContentLength);
//        validatePostHttpRequest(httpRequest);
//
//        Map<String, String> signUpData = httpRequest.parseBodyQueryParameter();
//
//        userService.signUpUser(signUpData);
//
//        try (FileInputStream input = new FileInputStream("/Users/tony/IdeaProjects/KUIT3_Backend-Java-Tomcat/webapp/index.html")) {    // TODO: status code 302 redirect 따로빼
//            return input.readAllBytes();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private void validatePostHttpRequest(HttpRequest httpRequest) throws IllegalArgumentException {    // TODO: 들어왔으면 올바른 header를 가지고 있는지 확인 해야겠지..? 여기서는 간단히 부분만 구현
//        String attributeValue = httpRequest.parseHeaderValue("Content-Type");// TODO: 이 놈~ 해야겠지
//        if(!Objects.equals(attributeValue, "application/x-www-form-urlencoded")) {
//            throw new IllegalArgumentException("이게 왜 여기로 넘어와, 올바른 형식이 아니야");
//        }
//    }
//}
