package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.util.IOUtils;
import model.User;
import webserver.controller.*;
import webserver.httprequest.HttpRequest;
import webserver.httpresponse.HttpResponse;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static http.util.HttpRequestUtils.parseQueryParameter;
import static webserver.httprequest.UrlPath.*;
import static webserver.httprequest.HttpMethod.*;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private final Repository repository;
    private Controller controller;
    public RequestHandler(Socket connection) {
        this.connection = connection;
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = HttpResponse.from(dos);


            // 요구사항 1
            // / 이거나 index.html이면 바디에 해당 파일을 넘겨야함.
            if(httpRequest.getMethod().equals(GET.getMethod()) && httpRequest.getUrl().equals(INDEX.getPath())){
                controller = new ForwardController();
            }

            if(httpRequest.getUrl().equals("/")){
                controller = new HomeController();
            }

            // 요구사항 2
            //  SignUp 버튼을 클릭하면 /user/form.html 화면으로 이동
            if(httpRequest.getUrl().equals(USER_FORM.getPath())){
                controller = new UserFormController();
            }
            // /user/signup
//            if(httpRequest.getMethod().equals(GET.getMethod()) &&httpRequest.getUrl().startsWith(USER_SIGNUP.getPath())){
//                String tmp = httpRequest.getUrl().split("\\?")[1];
//                Map<String,String> m = parseQueryParameter(tmp);
//                User user = new User(m.get("userId"), m.get("password"), m.get("name"), m.get("email"));
//                repository.addUser(user);
//                httpResponse.response302Header(INDEX.getPath());
//                return;
//            }

            // 요구사항 3
            if(httpRequest.getMethod().equals(POST.getMethod()) && httpRequest.getUrl().equals(USER_SIGNUP.getPath())){
                controller = new SignUpController();
            }


            // 요구사항 5
            if(httpRequest.getUrl().equals(USER_LOGIN_FILE.getPath())){
                controller = new LoginFormController();
            }

            if(httpRequest.getMethod().equals(POST.getMethod()) && httpRequest.getUrl().equals(USER_LOGIN.getPath())){
                controller = new LoginController();
            }

            // 요구사항 6 -> 문제점 현재 내 pc에서만의 문제인지 모르겠으나 쿠키 값이 여러개이고 ;으로 쿠키값이 끝난다
            if(httpRequest.getUrl().equals(USER_LIST.getPath())){
                controller = new ListController();
            }

            // 요구사항 7
            if (httpRequest.getUrl().endsWith(".css")) {
                controller = new CssController();
            }

            controller.excute(httpRequest,httpResponse);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

}