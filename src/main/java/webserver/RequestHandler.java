package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.util.IOUtils;
import model.User;
import webserver.http.HttpRequest;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.util.HttpRequestUtils.parseQueryParameter;
import static http.message.HttpHeader.*;
import static webserver.Url.*;
import static http.message.HttpMethod.*;
import static webserver.UserQueryKey.*;


public class RequestHandler implements Runnable{

    Socket connection;



    private final Repository repository;



    private final Path homePath = Paths.get(ROOT_URL.getUrl() + HOME_URL.getUrl());


    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        //try-with-resources 구문사용
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);


            HttpRequest httpRequest = HttpRequest.from(br);

            byte[] body = new byte[0];






            //서버에서 요청이 GET이고 .html로 끝난다면
            if (httpRequest.getMethod().equals(Get.getMethod()) && httpRequest.getUrl().endsWith(".html")) {
                body = Files.readAllBytes(Paths.get(ROOT_URL.getUrl() + httpRequest.getUrl()));
            }

            if (httpRequest.getUrl().equals("/")) {
                body = Files.readAllBytes(homePath);
            }



            //회원가입 submit제출했을 시
            if (httpRequest.getUrl().equals(USER_SIGNUP.getUrl()) ) {
                String queryString = IOUtils.readData(br, httpRequest.getRequestContentLength());
                Map<String, String> queryParameter = parseQueryParameter(queryString);
                User user = new User(queryParameter.get(USER_ID.getKey()), queryParameter.get(USER_PASSWORD.getKey()), queryParameter.get(USER_NAME.getKey()), queryParameter.get(USER_EMAIL.getKey()));
                repository.addUser(user);
                response302Header(dos,HOME_URL.getUrl());

                return;
            }

            //요구사항 5번
            if (httpRequest.getUrl().equals(LOGIN_URL.getUrl())) {
                String queryString = IOUtils.readData(br, httpRequest.getRequestContentLength());
                Map<String, String> queryParameter = parseQueryParameter(queryString);
                User user = repository.findUserById(queryParameter.get(USER_ID.getKey()));
                login(dos, queryParameter, user);
                return;
            }

            //요구사항6번
            if (httpRequest.getUrl().equals(USER_LIST.getUrl())) {
                if (!httpRequest.getCookie().equals("logined=true")) {
                    response302Header(dos, LOGIN_URL_HTML.getUrl());
                    return;
                }
                body = Files.readAllBytes(Paths.get(ROOT_URL.getUrl() + LIST_URL.getUrl()));
            }

            //요구사항7번
            if (httpRequest.getMethod().equals(Get.getMethod()) && httpRequest.getUrl().endsWith(".css")) {
                body = Files.readAllBytes(Paths.get(ROOT_URL.getUrl() + httpRequest.getUrl()));
                response200HeaderWithCss(dos, body.length);
                responseBody(dos, body);
                return;
            }



            response200Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes(HTTP200.getHeader());
            dos.writeBytes(ContextTypeHtml.getHeader());
            dos.writeBytes(ContentLength.getHeader() + lengthOfBodyContent + "\r\n");
            dos.writeBytes(Escape.getHeader());
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void login(DataOutputStream dos, Map<String, String> queryParameter, User user) {
        if (user != null && user.getPassword().equals(queryParameter.get(USER_PASSWORD.getKey()))) {
            response302HeaderWithCookie(dos,HOME_URL.getUrl());
            return;
        }
        response302Header(dos,LOGIN_FAILED_URL.getUrl());
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void response302Header(DataOutputStream dos, String path) {
        //302(임시 이동): 현재 서버가 다른 위치의 페이지로 요청에 응답하고 있지만 요청자는 향후 요청 시 원래 위치를 계속 사용해야 한다.
        try {
            dos.writeBytes(HTTP302.getHeader());
            dos.writeBytes( Location.getHeader()+ path + Escape.getHeader());
            dos.writeBytes(Escape.getHeader());
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void response302HeaderWithCookie(DataOutputStream dos, String path) {
        try {
            dos.writeBytes(HTTP302.getHeader() );
            dos.writeBytes(Location.getHeader() + path + Escape.getHeader());
            dos.writeBytes(SetCookie.getHeader() + Escape.getHeader());
            dos.writeBytes(Escape.getHeader());
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void response200HeaderWithCss(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes(HTTP200.getHeader());
            dos.writeBytes(ContextTypeCss.getHeader());
            dos.writeBytes(ContentLength.getHeader() + lengthOfBodyContent +Escape.getHeader());
            dos.writeBytes(Escape.getHeader());
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}