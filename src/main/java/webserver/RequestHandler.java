package webserver;

import db.MemoryUserRepository;
import http.util.controller.*;
import http.util.request.HttpRequest;
import http.util.request.UrlList;
import http.util.response.HttpResponse;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static db.MemoryUserRepository.getInstance;
import static http.util.HttpRequestUtils.parseQueryParameter;
import static http.util.IOUtils.readData;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    Controller controller = new ForwardController();





    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream();){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest request = HttpRequest.from(br);
            HttpResponse response = HttpResponse.from(dos);


            // GET
            if(request.getMethod().equals("GET")){

                // 홈
                if(request.getUrl().equals("/") || request.getUrl().equals("/index.html")){
                    controller = new HomeController();
                }

                // 예시 코드에 있던 방법. 아주 좋네요
                // html 요청시 반환
                if(request.getUrl().endsWith(".html")){
                    controller = new ForwardController();
                }

                if(request.getUrl().endsWith(".css")){
                    controller = new ForwardCssController();
                }

                // 유저 목록 화면
                if(request.getUrl().equals("/user/userList")){
                    controller = new UserListController();
                }
            }

            // POST
            if(request.getMethod().equals("POST")){
                // 회원가입 요청 POST
                if(request.getUrl().equals("/user/signup")){
                    controller = new SignUpController();
                }

                // 로그인 요청
                if(request.getUrl().equals("/user/login")){
                    controller = new LoginController();
                }
            }


            controller.execute(request, response);


        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }
}